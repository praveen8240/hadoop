
### 1. Create & Compile in Eclipse
1. Create a new Java project in Eclipse
2. Create a new class called `WeatherAnalysis`
3. Copy the code above into the class file
4. Export the project as a JAR file named `weather.jar`

### 2. Prepare Sample Data
Create a file named `weather_data.txt` with the exact fixed-width format:

```
01234520230101xxxxxxx 05.0 38.0
01234520230102xxxxxxx 06.2 32.5
01234520230103xxxxxxx 04.5 30.2
01234520230104xxxxxxx 12.5 34.8
01234520230105xxxxxxx 08.7 37.1
01234520230106xxxxxxx 11.2 36.3
```

### 3. Run on Hadoop

# Upload data to HDFS
hadoop fs -mkdir -p /user/input
hadoop fs -put weather_data.txt /user/input/

# Run the MapReduce job
hadoop jar weather.jar WeatherAnalysis /user/input /user/output

# View results
hadoop fs -cat /user/output/part-r-00000


### Expected Output

Hot Day 20230101 38.0
Cold Day 20230101 05.0
Cold Day 20230102 06.2
Cold Day 20230103 04.5
Hot Day 20230105 37.1
Hot Day 20230106 36.3


The program analyzes the fixed-width formatted weather data, exactly as specified. It:
1. Extracts the date from positions 6-14
2. Gets minimum temperature from positions 39-45
3. Gets maximum temperature from positions 47-53
4. Identifies hot days (max temp > 35.0) and cold days (min temp < 10)
5. Outputs the results in the format "Hot Day [date] [temp]" or "Cold Day [date] [temp]"