import unittest
from context import GeoPoint
from context import gen_random_point

class TestGenRandomPoint(unittest.TestCase):
    def test(self):
        topleft = GeoPoint(-102.96721, 47.99437)
        bottomright = GeoPoint(-102.96034, 47.99104)
        for i in range(100):
            point = gen_random_point(topleft, bottomright)
            self.assertLess(point.lng, bottomright.lng)
            self.assertGreater(point.lng, topleft.lng)
            self.assertLess(point.lat, topleft.lat)
            self.assertGreater(point.lat, bottomright.lat)

if __name__ == '__main__':
    unittest.main()