import random
import pandas as pd

def gen(min_lng, max_lng, min_lat, max_lat):
    lng = (max_lng - min_lng) * random.uniform(0, 1) + min_lng
    lat = (max_lat - min_lat) * random.uniform(0, 1) + min_lat
    return (lng, lat)

def main():
    start_min_lng = -103.02014
    start_min_lat = 48.00989
    start_max_lng = -103.00605
    start_max_lat = 48.01877

    end_min_lng = -102.90065
    end_min_lat = 48.01092 
    end_max_lng = -102.87937
    end_max_lat = 48.02022
    df = pd.DataFrame()
    df["Start Lng"] = ""
    df["Start Lat"] = ""
    df["End Lng"] = ""
    df["End Lat"] = ""
    for i in range(100):
        lng, lat = gen(start_min_lng, start_max_lng, start_min_lat, start_max_lat)
        df.loc[i, "Start Lng"] = lng
        df.loc[i, "Start Lat"] = lat
        lng, lat = gen(end_min_lng, end_max_lng, end_min_lat, end_max_lat)
        df.loc[i, "End Lng"] = lng
        df.loc[i, "End Lat"] = lat
    df.to_csv("/home/rzuo02/work/repast/app/src/main/resources/"
        + "repast/start_end_pair.csv", index=False) 

if __name__ == '__main__':
    main()