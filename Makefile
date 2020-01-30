
# --------------------------------------------------------------------
# Author: Menuka Warushavithana
# --------------------------------------------------------------------

.PHONY: build
build:
	gradle build

.PHONY: run-reg
run-reg:
	cd build/classes/java/main && java cs455.overlay.node.Registry

.PHONY: run-node
run-node:
	cd build/classes/java/main && java cs455.overlay.node.MessagingNode
