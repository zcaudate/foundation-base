build:
	docker run --rm --name scaffold --network host -v /var/run/docker.sock:/var/run/docker.sock -v $$(pwd):$$(pwd) -w $$(pwd) ghcr.io/zcaudate/testing-foundation:main  lein test

