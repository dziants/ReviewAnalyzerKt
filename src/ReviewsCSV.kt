/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dz.analyzers

import org.supercsv.cellprocessor.CellProcessorAdaptor
import java.io.*;
import java.nio.charset.Charset;

import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;
/**
 * A wrapper class that provides resources to read the CSV file that contains Amazon product reviews, a line at a time,
 * and load the relevant data from the current line. .
 * @author David
 */
public class ReviewsCSV(path1: String, path2: String) {

    /**
     * Holds a record of the relevant data from a line in csv file
     * @param id The id number of the record
     * @param productId The id of the product being reviewed
     * @param profileName The profile name ofthe person giving the review
     * @param text The text of the review
     */
    public data class ReviewsRow(var id: String="", var productId: String="", var profileName: String="", var text: String="") {
        // Refer only to columns that are relevant for task - Need to have default values as usage needs default constructor
    }

    /**
     * Performs any initializations that are not done at instantiation
     * In practice this skipping the first header line of the csv file.
     */
    public fun initialize() {

        // Skip first header line.
        var dummyHeader: Array<String> = csvReader!!.getHeader(true);


    }

    /**
     * Close all CSV resources after running analyzer.
     */
    public fun closeAll() {
        try {

            csvReader!!.close();
            inpStrmRdr!!.close();
            inpStrm!!.close();

        } catch (e: IOException) {
            System.out.println("An IO exception occured when trying to close ReviewsCSV.");
            System.out.println("The exception is: " + e.getLocalizedMessage());
            return; //Do not propogate exception 
        } catch (t: Throwable) { // Catch other exceptions - such as unrecognized charset
            System.out.println("An exception occured when trying to close ReviewsCSV.");
            System.out.println("The exception is: " + t.getLocalizedMessage());
            return; //Do not propogate exception

        }
    }

    /**
     * Navigates to the next row in the CSV file, loading the relevant property.
     * Since this delegates to the Java csvReader method "read", which returns null when no more data, so we allow null
     * to be returned here.
     */
    public fun nextRow(): ReviewsRow? {
        try {
            return csvReader!!.read(reviewsRow, *reviewsRowNameMapping);
         } catch (e: IOException) {
            System.out.println("An IO exception occured when trying to read ReviewsCSV.");
            System.out.println("The exception is: " + e.getLocalizedMessage());
            throw e; //Propogate exception 
        } catch (t: Throwable) { // Catch other exceptions - such as unrecognized charset
            System.out.println("An exception occured when trying to read ReviewsCSV.");
            System.out.println("The exception is: " + t.getLocalizedMessage());
            throw t; //Propogate exception 

        }
    }

    /**
     * Returns the current row in the CSV file. This was loaded when navigated to it.
     * Since this was obatined by Java csvReader method "read", that returns null when no more data, we allow null
     * to be returned here.
     */
    public fun getRow(): ReviewsRow? {
        return reviewsRow;
    }

    /**
     * Private helper function to obtain CSV bean reader.
     */
    private fun obtCsvBeanReaderStream(path1: String, path2: String): CsvBeanReader {
        try {
            try {
                inpStrm = FileInputStream(path1);
            } catch (e: IOException) {
                inpStrm = FileInputStream(path2);
            }
            inpStrmRdr = InputStreamReader(inpStrm, Charset.forName("UTF8"));
            // Note that if used STANDARD_PREFERENCE, if rewriting csv,
            // both c/r and l/f characters would be written at end of line.
            return CsvBeanReader(inpStrmRdr, CsvPreference.EXCEL_PREFERENCE);
        } catch (e: IOException) {
            System.out.println("An IO exception occured when trying to initialise ReviewsCSV for csv file: ${path2}");
            System.out.println("The exception is: " + e.getLocalizedMessage());
            throw e; //Propogate exception
        } catch (t: Throwable) { // Catch other exceptions - such as unrecognized charset
            System.out.println("An exception occured when trying to initialise ReviewsCSV for csv file:  ${path2}");
            System.out.println("The exception is: " + t.getLocalizedMessage());
            throw t; //Propogate exception

        }
    }

    private var csvReader: CsvBeanReader? = obtCsvBeanReaderStream(path1, path2) ;
    private var inpStrm: InputStream? = null;
    private var inpStrmRdr: Reader? = null;
            ;
    private var reviewsRow: ReviewsRow? = ReviewsRow("","","","");
    // Mapping can only work if nulls are provided where no data.
    private val reviewsRowNameMapping: Array<String?> = arrayOf(
            "id", "productId", null, "profileName", null, null, null, null, null, "text");

}


