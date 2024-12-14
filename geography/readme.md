# Geometry Tool Kit (GTK) - Geography

Geography provides tools and data structures for classifying a location according to jurisdictions.

## Credits
Data is sourced from https://www.geoboundaries.org/

## Notes
- We adopt the geoboundaries.org standard of referring to administrative boundaries at level 0, 1, 2, and 3 (where available), where 0 corresponds to national boundaries, 1, to states, and 2 to counties. Different nations may call these levels different things (municpalities, areas, etc.)
- We truncated the data sources with (Lon, Lat) to 7 decimal places, where the 6th decimal place gives precision to within approximately 4.5" at the equator. 
