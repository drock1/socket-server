socket-server
=============

This is a simple socket server designed to handle messages packaged in the following format:

- 1 Byte version
- 2 Byte type
- 4 Byte userID
- Variable payload

The system is designed so that adding handlers for new message types is extremely easy and does not
require a deeper understanding of the underlying systems.

Next steps:

1) Replace proprietary wire protocol with Google protobuffers library
