## **Apache Pig Installation and Practice Examples on Cloudera**  

### **What is Apache Pig?**  
Apache **Pig** is a high-level scripting language used for processing large datasets in **Hadoop**. It simplifies **MapReduce programming** using **Pig Latin** scripts.

---

## **Step 1: Check if Pig is Installed**
Since you're using **Cloudera**, Pig should already be installed. Verify by running:  
```bash
pig -version
```
If it's installed, you'll see the version. If not, install it using:  
```bash
sudo yum install -y pig
```

---

## **Step 2: Start Pig**
Pig can run in two modes:  
1. **Local Mode** (for small datasets):  
   ```bash
   pig -x local
   ```
2. **HDFS Mode** (for large datasets, requires Hadoop):  
   ```bash
   pig
   ```

---

## **Step 3: Create a Sample Data File**
Create a sample **employees.txt** file:  
```bash
nano employees.txt
```
Add the following data and save:  
```
1,John,28,Engineering
2,Alice,30,HR
3,Bob,25,Marketing
4,Eve,35,Finance
```
Upload the file to HDFS:  
```bash
hdfs dfs -mkdir -p /user/cloudera/pig_data
hdfs dfs -put employees.txt /user/cloudera/pig_data/
```

---

## **Step 4: Load Data in Pig**
Start Pig and load the data:  
```pig
employees = LOAD '/user/cloudera/pig_data/employees.txt' USING PigStorage(',') 
           AS (id:int, name:chararray, age:int, department:chararray);
```
Check if data is loaded:  
```pig
DUMP employees;
```

---

## **Step 5: Basic Transformations**
### **Filter Employees Above Age 28**
```pig
older_employees = FILTER employees BY age > 28;
DUMP older_employees;
```

### **Project Only Names and Departments**
```pig
names_dept = FOREACH employees GENERATE name, department;
DUMP names_dept;
```

### **Group Employees by Department**
```pig
grouped = GROUP employees BY department;
DUMP grouped;
```

### **Count Employees per Department**
```pig
count_by_dept = FOREACH grouped GENERATE group AS department, COUNT(employees);
DUMP count_by_dept;
```

---

## **Step 6: Store Data in HDFS**
To store processed data:  
```pig
STORE count_by_dept INTO '/user/cloudera/pig_output' USING PigStorage(',');
```
Check output in HDFS:  
```bash
hdfs dfs -ls /user/cloudera/pig_output
hdfs dfs -cat /user/cloudera/pig_output/part-r-00000
```

---

## **Step 7: Use Pig with Hive**
You can save Pig results to Hive tables:  
```pig
STORE employees INTO 'default.employees_hive' USING org.apache.hive.hcatalog.pig.HCatStorer();
```
Verify in Hive:  
```sql
SELECT * FROM employees_hive;
```

---

## **Step 8: Exit Pig**
To exit the Pig shell, type:  
```pig
quit;
```

---
