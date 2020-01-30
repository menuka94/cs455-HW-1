
# --------------------------------------------------------------------
# Author: Menuka Warushavithana
# --------------------------------------------------------------------

.PHONY: build
build:
	gradle build

.PHONY: run-reg
run-reg:
	gradle build
	cd build/classes/java/main && java -cp ../../../libs/cs455-1-1.0-SNAPSHOT.jar cs455.overlay.node.Registry 5600

.PHONY: run-node
run-node:
	cd build/classes/java/main && java -cp ../../../libs/cs455-1-1.0-SNAPSHOT.jar cs455.overlay.node.MessagingNode localhost 5600
