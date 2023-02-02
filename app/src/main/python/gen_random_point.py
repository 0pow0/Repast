from GeoPoint import GeoPoint
import pandas as pd
import random
import argparse

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('n')
    parser.add_argument('start')
    parser.add_argument('dest')
    parser.add_argument('output')
    args = parser.parse_args()
    return args

def main():
    args = parse_args()
    start = [float(e) for e in args.start.split(' ')]
    dest = [float(e) for e in args.dest.split(' ')]
    start = [GeoPoint(start[i], start[i+1]) for i in range(0, len(start), 2)]
    dest = [GeoPoint(dest[i], dest[i+1]) for i in range(0, len(dest), 2)]
    print(start)
    print(dest)
    n = int(args.n)
    df = pd.DataFrame()
    df["Start Lng"] = ""
    df["Start Lat"] = ""
    df["End Lng"] = ""
    df["End Lat"] = ""
    for i in range(n):
       start_point = gen_random_point(start[0], start[1]) 
       dest_point = gen_random_point(dest[0], dest[1])
       print(start_point)
       print(dest_point)
       print()
       df.loc[i, "Start Lng"] = start_point.lng
       df.loc[i, "Start Lat"] = start_point.lat
       df.loc[i, "End Lng"] = dest_point.lng
       df.loc[i, "End Lat"] = dest_point.lat
    # pd.set_option("display.precision", 20)
    print(df)
    df.to_csv(args.output, index=False)

def gen_random_point(topleft:GeoPoint, bottomright:GeoPoint):
    delta_lng = bottomright.lng - topleft.lng  
    assert delta_lng > 0
    delta_lat = topleft.lat - bottomright.lat
    assert delta_lat > 0
    return GeoPoint(topleft.lng + delta_lng * random.random(), bottomright.lat + delta_lat * random.random())

if __name__ == '__main__':
    main()