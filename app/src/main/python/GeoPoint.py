class GeoPoint:
    def __init__(self, lng, lat) -> None:
        self.lng = lng
        self.lat = lat
    
    def __str__(self) -> str:
        return 'GeoPoint Lat = ' + str(self.lng) + ' Lat = ' + str(self.lat)

    def __repr__(self) -> str:
        return 'GeoPoint Lat = ' + str(self.lng) + ' Lat = ' + str(self.lat)
