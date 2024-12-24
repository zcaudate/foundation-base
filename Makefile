docker-test:
	docker run --rm --name foundation-base-dev --network host -v /var/run/docker.sock:/var/run/docker.sock -v $$(pwd):$$(pwd) -w $$(pwd) ghcr.io/zcaudate-xyz/infra-foundation-clean:main lein test
	
docker-repl:
	docker run -it --rm --name foundation-base-dev -p 5432:5432 -p 51311:51311 -v /var/run/docker.sock:/var/run/docker.sock -v $$(pwd):$$(pwd) -w $$(pwd) ghcr.io/zcaudate-xyz/infra-foundation-clean:main lein repl

