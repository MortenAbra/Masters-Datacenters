import docker

client = docker.from_env()

#Hello world example
print(client.containers.run("alpine", ["echo", "hello", "world"]))

#Run container in background
background_container = client.containers.run("bfirsh/reticulate-splines", detach=True)

#List all running containers
for container in client.containers.list():
    print(container.id)

#Stop all running containers
#for container in client.containers.list():
#    container.stop()

#Initialise docker swarm
#client.swarm.init(advertise_addr='eth0', listen_addr='0.0.0.0:8080', force_new_cluster=False, default_addr_pool=['10.20.0.0/16'], subnet_size=24, snapshot_interval=5000, log_entries_for_slow_followers=1200)
client.swarm.join(remote_addrs='10.20.0.0', join_token=client.swarm.get_unlock_key())
