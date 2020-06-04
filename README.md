# yadi.java 
YADI (Yet Another DLMS implementation) is a library for communication with metering devices using the DLMS standard. Check the wiki for code examples!

Master: [![Build Status](https://travis-ci.org/pfaco/yadi.java.svg?branch=master)](https://travis-ci.org/pfaco/yadi.java)
Dev: [![Build Status](https://travis-ci.org/pfaco/yadi.java.svg?branch=dev)](https://travis-ci.org/pfaco/yadi.java)

### Highlights
	* Application layer (COSEM) decoupled from link layer
	* HDLC and Wrapper as link layer protocols
	* MODE-E connection
	* PUBLIC/LLS/HLS authentication association
	* AUTHENTICATION/ENCRYPTION security
	* Long name referencing
	* Serial and TCP implementations provided

### Dependencies:
	* jSSC

## Changelog 

### Version 1.0.11 (2020.Jun)
Java version 1.8

### Version 1.0.10 (2020.Jun)
Optimized Emode connection

### Version 1.0.9 (2020.May)
Added support for short name read and write
Fixed bugs in WrapperLinkLayer

### Version 1.0.8 (2020.Apr)
Added DlmsItem and DlmsParser

### Version 1.0.7 (2020.Apr)
Updated Java version and necessary plugins

### Version 1.0.6 (2020.Apr)
Uses jSerialComm as serial communication due to incompability of jSSC and Windows 10

### Version 1.0.0 (2019.Jan.30)
Revert back to jSSC dependency for serial port.

### Version 1.0.0 (2018.Aug.24)
Added Get-Request-With-Block support.
Added Set-Request-With-Block support.
Modified serial dependency lib to jSerialComm.

### Version 0.0.3a (2018.Feb.05)
Added tests
Solved bugs related to HDLC addressing

### Version 0.0.2a (2018.Feb.03)
Improved the documentation (javadoc) in the source code

### Version 0.0.1a (2018.Feb.03)
First released version.
