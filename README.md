# docker_jenkins_auto
how to configure automatically jenkins 2.235.1 in a docker container with some plugins pre-installed
Please update the init.groovy with your user account and password to create instead of your_user your_password

if you need other jenkins version please update the Dockerfile : 
ENV JENKINS_VERSION ${JENKINS_VERSION:-2.235.1}
and don't forget to update with correct sha-256 for the jenkins.war 
# to build the docker image and run it
docker-compose up


