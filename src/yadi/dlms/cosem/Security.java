/*
 * YADI (Yet Another DLMS Implementation)
 * Copyright (C) 2018 Paulo Faco (paulofaco@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package yadi.dlms.cosem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import yadi.dlms.DlmsException;
import yadi.dlms.DlmsException.DlmsExceptionReason;
import yadi.dlms.cosem.CosemParameters.SecurityType;

public class Security {

	private static final int SC_ENCRYPTION = 0x20;
	private static final int SC_AUTHENTICATION = 0x10;
	private static final int SC_AUTHENTICATION_ENCRYPTION = 0x30;
	private static Cipher cipher;
	private static SecureRandom sr = new SecureRandom();
	static {
		try {
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
			sr.setSeed(sr.generateSeed(16));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	static byte[] authenticatedEncryption(CosemParameters params, byte[] data) throws DlmsException {
		if (params.securityType == SecurityType.NONE) {
			return data;
		}
		
		int sc = 0;
		switch (params.securityType) {
		case AUTHENTICATION:
			sc = SC_AUTHENTICATION;
			byte[] authData = new byte[params.ak.length + data.length + 1];
			authData[0] = SC_AUTHENTICATION;
			System.arraycopy(params.ak, 0, authData, 1, params.ak.length);
			System.arraycopy(data, 0, authData, params.ak.length+1, data.length);
			byte[] mac = aesGcm(new byte[0], authData, params);
			byte[] data_ = new byte[data.length + mac.length];
			System.arraycopy(data, 0, data_, 0, data.length);
			System.arraycopy(mac, 0, data_, data.length, mac.length);
			data = data_;
			break;
		case AUTHENTICATION_ENCRYPTION:
			sc = SC_AUTHENTICATION_ENCRYPTION;
			authData = new byte[params.ak.length + 1];
			authData[0] = SC_AUTHENTICATION_ENCRYPTION;
			System.arraycopy(params.ak, 0, authData, 1, params.ak.length);
			data = aesGcm(data, authData, params);
			break;
		case ENCRYPTION:
			sc = SC_ENCRYPTION;
			data = aesGcm(data, new byte[0], params);
			break;
		default:
			throw new IllegalStateException();
		}
		
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(sc);
			stream.write(ByteBuffer.allocate(4).putInt(params.invocationCounter).array());
			stream.write(data);
			return stream.toByteArray();
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
	public static byte[] reverseAuthenticatedEncryption(CosemParameters params, CosemConnection connection, byte[] data) throws DlmsException {
		switch (data[0] & 0xff) {
		case SC_AUTHENTICATION:
			connection.serverInvocationCounter = ByteBuffer.allocate(4).put(Arrays.copyOfRange(data, 1, 5)).getInt(0);
			return aesGcmReverse(new byte[0], Arrays.copyOfRange(data, 5, data.length), params, connection);
			
		case SC_AUTHENTICATION_ENCRYPTION:
			byte[] authData = new byte[params.ak.length + 1];
			authData[0] = SC_AUTHENTICATION_ENCRYPTION;
			System.arraycopy(params.ak, 0, authData, 1, params.ak.length);
			connection.serverInvocationCounter = ByteBuffer.allocate(4).put(Arrays.copyOfRange(data, 1, 5)).getInt(0);
			return aesGcmReverse(Arrays.copyOfRange(data, 5, data.length), authData, params, connection);
			
		case SC_ENCRYPTION:
			connection.serverInvocationCounter = ByteBuffer.allocate(4).put(Arrays.copyOfRange(data, 1, 5)).getInt(0);
			return aesGcmReverse(Arrays.copyOfRange(data, 5, data.length), new byte[0], params, connection);
			
		default:
			return data;
		}
	}
	
	static byte[] aesGcm(byte[] data, byte[] authData, CosemParameters params) throws DlmsException {
		try {
			params.invocationCounter++;
			byte[] iv = getIv(params.systemTitle, params.invocationCounter);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(params.ek, "AES"), new GCMParameterSpec(12 * Byte.SIZE, iv));
			cipher.updateAAD(authData);
			return cipher.doFinal(data);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (AEADBadTagException e) {
			throw new DlmsException(DlmsExceptionReason.SECURITY_FAIL);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
	}
	
	static byte[] aesGcmReverse(byte[] encrypted, byte[] authData, CosemParameters params, CosemConnection connection) throws DlmsException {
		try {
			byte[] iv = getIv(connection.serverSysTitle, connection.serverInvocationCounter);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(params.ek, "AES"), new GCMParameterSpec(12 * Byte.SIZE, iv));
			cipher.updateAAD(authData);
			return cipher.doFinal(encrypted);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (AEADBadTagException e) {
			throw new DlmsException(DlmsExceptionReason.SECURITY_FAIL);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
	}
	
	private static byte[] getIv(byte[] sysTitle, int invocationCounter) {
		byte[] iv = new byte[sysTitle.length + 4];
		byte[] iCounter = ByteBuffer.allocate(4).putInt(invocationCounter).array();
		System.arraycopy(sysTitle, 0, iv, 0, sysTitle.length);
		System.arraycopy(iCounter, 0, iv, sysTitle.length, 4);
		return iv;
	}

	static byte[] generateChallanger(CosemParameters params) {
		byte[] random = new byte[params.challengerSize];
		sr.nextBytes(random);
		return random;
	}
	
	static byte[] processChallanger(CosemParameters params, CosemConnection connection) throws DlmsException {
		try {
			switch (params.authenticationType) {
			case PUBLIC:
			case LLS:
				throw new IllegalStateException();
			case HLS:
				return Security.aes128(connection.challengeServerToClient, params.llsHlsSecret);
			case HLS_MD5:
				return Security.md5(connection.challengeServerToClient, params.llsHlsSecret);
			case HLS_SHA1:
				return Security.sha1(connection.challengeServerToClient, params.llsHlsSecret);
			case HLS_GMAC:
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				data.write(SC_AUTHENTICATION);
				data.write(params.ak);
				data.write(connection.challengeServerToClient);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				stream.write(SC_AUTHENTICATION);
				stream.write(ByteBuffer.allocate(4).putInt(params.invocationCounter+1).array());
				stream.write(Security.aesGcm(new byte[0], data.toByteArray(), params));
				return stream.toByteArray();
			default:
				throw new IllegalArgumentException();
			}
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
	static boolean verifyChallenger(CosemParameters params, CosemConnection connection, byte[] data) throws DlmsException {
		if (data == null || data.length == 0) {
			return false;
		}
		try {
			byte[] calculated = new byte[0];
			switch (params.authenticationType) {
			case PUBLIC:
			case LLS:
				throw new IllegalStateException();
			case HLS:
				calculated = Security.aes128(connection.challengeClientToServer, params.llsHlsSecret);
				break;
			case HLS_MD5:
				calculated = Security.md5(connection.challengeClientToServer, params.llsHlsSecret);
				break;
			case HLS_SHA1:
				calculated = Security.sha1(connection.challengeClientToServer, params.llsHlsSecret);
				break;
			case HLS_GMAC:
				if (data[0] != SC_AUTHENTICATION) {
					return false;
				}
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				stream.write(SC_AUTHENTICATION);
				stream.write(params.ak);
				stream.write(connection.challengeClientToServer);
				connection.serverInvocationCounter = ByteBuffer.allocate(4).put(Arrays.copyOfRange(data, 1, 5)).getInt(0);
				data = Arrays.copyOfRange(data, 5, data.length);
				CosemParameters cosemParams = new CosemParameters();
				cosemParams.setSystemTitle(connection.serverSysTitle);
				cosemParams.setInvocationCounter(connection.serverInvocationCounter-1);
				cosemParams.setEk(params.ek);
				calculated = Security.aesGcm(new byte[0], stream.toByteArray(), cosemParams);
				break;
			default:
				throw new IllegalArgumentException();
			}
			return Arrays.equals(data, calculated);
		} catch (IOException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}		
	}
	
	private static byte[] aes128(byte[] challenger, byte[] secret) throws DlmsException {
		try {
			int len = Math.max(challenger.length, secret.length);
			while ((len & 0x0F) != 0) {
				len++;
			}
			byte[] key = new byte[len];
			byte[] data = new byte[len];
			System.arraycopy(secret, 0, key, 0, secret.length);
			System.arraycopy(challenger, 0, data, 0, challenger.length);
		    Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
		    return cipher.doFinal(data);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
	}

	private static byte[] md5(byte[] challenger, byte[] secret) throws DlmsException {
		return getDigest(challenger, secret, "MD5");
	}
	
	private static byte[] sha1(byte[] challenger, byte[] secret) throws DlmsException {
		return getDigest(challenger, secret, "SHA-1");
	}
	
	private static byte[] getDigest(byte[] challenger, byte[] secret, String algorithm) throws DlmsException {
		try {
			byte[] data = new byte[challenger.length + secret.length];
			System.arraycopy(challenger, 0, data, 0, challenger.length);
			System.arraycopy(secret, 0, data, challenger.length, secret.length);
			return java.security.MessageDigest.getInstance(algorithm).digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new DlmsException(DlmsExceptionReason.INTERNAL_ERROR);
		}
	}
	
}
