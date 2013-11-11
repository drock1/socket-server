socket-server
=============

This is a simple socket server designed to handle messages packaged in the following format:

- 1 Byte version
- 2 Byte message type
- 4 Byte userID
- Variable payload

The system is designed so that adding handlers for new message types is extremely easy and does not
require a deeper understanding of the underlying systems.