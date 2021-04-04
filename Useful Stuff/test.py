import requests

data={
    'name':'Pyserver',
    'ip':'0.0.0.0',
    'port':'5000',
    'available':True,
    'sharedDir':'/home/wolder/shared_storage',
    'type': 'container',
    'dockerWorkload': {
        'container_Dir':'/home/wolder/shared_storage',
        'container_image':'pyserver',
        'container_ID' :"sweet_noether",
        'checkpoint' :False,
        'container_CPDir':'/home/wolder/shared_storage',
        }
    }

data2={
    'wl_name':'Pyserver', 
    'target_guest':{
        "ip":"192.168.43.102",
        "port":"8080"
    }
}

data_new_container={
    'Identifier':'Python Flask Server',
    'AccessIP':'',
    'AccessPort':'',
    'Available':True,
    'SharedDir':'/home/wolder/shared_storage',
    'Type':'Container',
    'Containerproperties': {
        'ContainerID':'',
        'Image':'',
        'Checkpoint':'',
    }       
}

data_new_VM={
    'Identifier':'Python Flask Server',
    'AccessIP':'',
    'AccessPort':'',
    'Available':True,
    'SharedDir':'/home/wolder/shared_storage',
    'Type':'VM',
    'VMProperties': {
        'DomainName':'',
        'ConnectionURI':'',
    }       
}


host = "192.168.122.72"
#host = "localhost"
#192.168.122.1
#host = "192.168.43.102"
#requests.post('http://' + host + ':8080/workloads', json=data)
requests.post('http://' + host + ':8080/migrate', json=data2)


