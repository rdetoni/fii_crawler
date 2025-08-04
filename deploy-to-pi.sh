#!/bin/bash
set -x
# Set your remote server's SSH details
SSH_USER="admin"
SSH_HOST="192.168.0.51"

#Put the whole project on a tar file
tar -zcvf fii_crawler.tar.gz fii_crawler

#Send project to PI
scp fii_crawler.tar.gz $SSH_USER@$SSH_HOST:/home/admin/docker-images

# Delete local tarball after transfer
rm fii_crawler.tar.gz

# SSH into the remote server
ssh $SSH_USER@$SSH_HOST << EOF
	set -x

	# Extract project contents
	tar -xvzf /home/admin/docker-images/fii_crawler.tar.gz -C /home/admin/docker-images/


    # Build the Docker image
	cd /home/admin/docker-images/fii_crawler
	mvn package
	docker build -t fii_crawler:latest -f Dockerfile .

    # Optionally, push the image to a Docker registry
    # docker push your-registry-url/$IMAGE_NAME

    # Run your Docker container
    docker run -d -p 7000:7000 fii_crawler:latest
EOF