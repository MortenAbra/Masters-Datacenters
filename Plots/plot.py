import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

x = []
y = []

file = pd.read_csv('/home/wolder/Documents/Projects/Masters-Datacenters/Plots/data.csv', delimiter=',')
print(file)

workload_label = file['Name']
imagesize = file['Imagesize (mb)']


processing = file['Processing']
disk = file['Disk']
memory = file['Memory']
network = file['Network']
migrationtime = file['Combined']



fig, ax1 = plt.subplots()

plt.title('TODO Title')

color = 'tab:blue'
ax1.set_xlabel('Workload')
ax1.set_ylabel('Migration Time (s)', color=color)
ax1.plot(workload_label, migrationtime, color=color)
ax1.tick_params(axis='y', labelcolor=color)

ax2 = ax1.twinx()  # instantiate a second axes that shares the same x-axis

color = 'tab:red'
ax2.set_ylabel('Image Size (MB)', color=color)  # we already handled the x-label with ax1
ax2.plot(workload_label, imagesize, color=color)
ax2.tick_params(axis='y', labelcolor=color)

fig.tight_layout()  # otherwise the right y-label is slightly clipped
plt.show()


fig, ax1 = plt.subplots()

plt.title('TODO Title2')

z = np.polyfit(imagesize, migrationtime, 1)
p = np.poly1d(z)

color = 'tab:blue'
ax1.set_xlabel('Image Size (MB)')
ax1.set_ylabel('Migration Time (s)', color=color)
ax1.plot(imagesize, migrationtime, color=color)
ax1.tick_params(axis='y', labelcolor=color)
ax1.plot(imagesize,p(imagesize),"r--")

print("Trendline equation:  y=%.6fx+(%.6f)"%(z[0],z[1]))


fig.tight_layout()  # otherwise the right y-label is slightly clipped
plt.show()