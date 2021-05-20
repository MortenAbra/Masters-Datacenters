import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

x = []
y = []

file = pd.read_csv('/home/wolder/Documents/Projects/Masters-Datacenters/Plots/data3.csv', delimiter=',')
print(file)

workload_label = file['Name']
imagesize = file['Imagesize (mb)']


processing = file['Processing']
disk = file['Disk']
memory = file['Memory']
network = file['Network']
migrationtime = file['Combined']

    

def pieChartOfTaskRatios():
    processingRatios = []
    diskRatios = []
    memoryRatios = []
    networkRatios = []

    for index, row in file.iterrows():
        total = row['Processing'] + row['Disk'] + row['Memory'] + row['Network']
        processingRatios.append(row['Processing'] / total)
        diskRatios.append(row['Disk'] / total)
        memoryRatios.append(row['Memory'] / total)
        networkRatios.append(row['Network'] / total)


    df1 = pd.DataFrame(data=processingRatios)
    df2 = pd.DataFrame(data=diskRatios)
    df3 = pd.DataFrame(data=memoryRatios)
    df4 = pd.DataFrame(data=networkRatios)

    ratios = [df1[0].mean(), df2[0].mean(), df3[0].mean(), df3[0].mean()]

    explode = (0.01, 0.01, 0.1, 0.01)
    labels=["Processing", "Disk", "Memory", "Network"]
    fig1, ax1 = plt.subplots()
    ax1.pie(ratios, explode=explode, labels=labels, autopct='%1.1f%%',
            shadow=False, startangle=180)
    ax1.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.

    plt.title("Average migration task duration ratio")
    plt.show()


def compareImagesAndImageSize():
    fig, ax1 = plt.subplots()
    plt.title('Image size and migration duration')

    color = 'tab:blue'
    ax1.set_xlabel('Workload by name')
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

def compareImageSizeAndCombinedTime():
    fig, ax1 = plt.subplots()

    plt.title('Imagesize compared to migration duration')

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

def workloadTaskDistribution():
    fig, ax = plt.subplots()
    width = 0.35       # the width of the bars: can also be len(x) sequence


    ax.bar(workload_label, processing, width,  label='Processing')
    ax.bar(workload_label, disk, width, bottom=processing,label='Disk')
    ax.bar(workload_label, memory, width, bottom=processing, label='Memory')
    ax.bar(workload_label, network, width, bottom=processing, label='Network')

    ax.set_ylabel('Migration Time (s)')
    ax.set_xlabel('Workload by name')
    ax.set_title('Migration Time per workload')
    ax.legend()

    plt.show()


def elspotPlot():
    file = pd.read_csv('/home/wolder/Documents/Projects/Masters-Datacenters/Plots/elspotprices.csv', delimiter=',')
    print(file)

    x_label = file['HourDK']
    y_values = file['SpotPriceDKK']
    y_mean = [np.mean(y_values)]*len(x_label)

    threshold_top = y_values.mean() + y_values.mean() * 0.1
    threshold_middle = y_values.mean()
    threshold_but = y_values.mean() - y_values.mean() * 0.1

    top_list = [threshold_top]*len(x_label)
    middle_list = [threshold_middle]*len(x_label)
    buttom_list = [threshold_but]*len(x_label)

    x = np.linspace(0, 24, 24)
    

    fig, ax = plt.subplots()
    ax.plot(x, y_values)  
    # Plot the average line
    #mean_line = ax.plot(x,y_mean, label='Mean', linestyle='--')
    
    ax.locator_params(axis="x", nbins=24)
    
    ax.axhline(threshold_top, color='green', lw=2, alpha=0.5)
    #ax.axhline(threshold_middle, color='orange', lw=2, alpha=0.5)
    ax.axhline(threshold_but, color='red', lw=2, alpha=0.5)
    ax.fill_between(x, y_values, top_list, where=y_values > threshold_top,
                color='green', alpha=0.2, interpolate=True)

    ax.fill_between(x, y_values, buttom_list, where=threshold_but > y_values,
                color='red', alpha=0.2, interpolate=True)


    ax.set_title("EnergiDataService Migrate Threshold")

    ax.set_xlabel("Time of day (Hours)")
    ax.set_ylabel("Elspot prices (DKK)")

    plt.show()


workloadTaskDistribution()
pieChartOfTaskRatios()
compareImageSizeAndCombinedTime()
compareImagesAndImageSize()
#elspotPlot()