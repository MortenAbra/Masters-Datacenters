import time
import pandas as pd
import json
import requests
import csv

url = 'https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100'

def fetchElectricityPricing(url):
    with requests.get(url) as response:
        if response.status_code == 200:
            print("passed")
            data = response.json()


            saveJson(data['result']['records'])

            df = pd.read_json('data.json')
            df.to_csv('results.csv')

            findCertainValue(df)

def saveJson(data):
    with open('data.json', 'w') as f:
        json.dump(data, f)

def findCertainValue(df):
    #find values at matching price area



fetchElectricityPricing(url)