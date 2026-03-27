# FlintersVietNam

## How to Run the Program

Run the program using the following command:

```bash
java -cp target/classes org.example.Main --input <path_to_input_csv> --output <output_directory>
```

Replace `<path_to_input_csv>` with the path to your input CSV file and `<output_directory>` with the desired output directory.

Example:

```bash
java -cp target/classes org.example.Main --input C:\Users\lequa\Downloads\ad_data\ad_data.csv --output result/
```

The program will print the processing times to the console.

## Libraries Used

The project uses the Java standard library. No external libraries or dependencies are required.

## Processing Time for the 1GB File

Example output:
- Time to read CSV: 22.2545 ms
- Time to process: 1608.9194 ms
- Total time: 1631.1739 ms
