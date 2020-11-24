# HistoricalWeather
NOAA Data - Precipitation and Temperature Change

## How To Run
`java HistoricalWeather.java "days-of-precip" "mia"`

## Assumptions
- "days-of-precip" function was written to determine what the average days of precipitation per year PER city is; it will not output the city with the most precip as the project description (mistakenly?) describes in the prompt
- Some Exceptions (time permitted) have been added to account for breakage from the user i.e. invalid function name, year, month, etc.
- For daysOfPrecip(), `totalYears++` is calculated assuming the CSV is ordered as is, by city, by year
  - To account for a different unordered CSV being used with this program, a sort would be done (using the Date column) to properly count how many years average precipitation is being calculated for

## CSV Specific Notes
- This program has been specifically written for the NOAA provided CSV
- `FileReader` and `line.split` account for extra comma found in city "Name" column
- File name "/noaa_historical_weather_10yr.csv" has been hard coded into program
  - Code could be modified using file input to ask user for file they would like read
