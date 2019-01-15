/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dz.analyzers

/**
 * Requirements (Parts 1,2 and 3)
 * File Reviews.csv was downloaded.
 * We are interested in:
 * 1) Finding 1000 most active users (profile names)
 * 2) Finding 1000 most commented food items (item ids).
 * 3) Finding 1000 most used words in the reviews

 * Structure of CSV file:-
 * Column #  | Column Name
 * --------  | -------------------------------------------
 * 0           Id
 * 1           ProductId
 * 2           UserId
 * 3           ProfileName
 * 4           HelpfulnessNumerator
 * 5           HelpfulnessNumerator
 * 6           HelpfulnessDenominator
 * 7           Score
 * 8           Time
 * 9           Summary
 * 10          Text

 * The following fields contain free text including possible "," (comma symbol),
 * or quote marks, and so are sometimes quoted:- ProfileName (3),Summary (9),Text (10)
 * The other fields can be assumed never to be quoted.

 * For the purpose of the task,
 * A choice is made to ignore Summary column, and the text analysis will be made
 * on Text only.
 * So the columns that need to be considered for the task are:-
 * Id, ProductId, ProfileName, Text (Id is used for reference purposes only)

 * Software Design:

 * Main class ReviewAnalyzer
 * main method:-
 * Instantiates instance of ReviewAnalysisEngine
 * Performs analysis using engine which return results.
 * Write results to standard output.

 * Class to encapsulate csv file.
 * It is assumed that the path of the CSV file is either <User Home>\Reviews.csv or .\Reviews.csv
 * Name: ReviewsCSV
 * Inner Class ReviewsRow. Structure to hold data of row of CSV file.
 * Not all elements of record are relevant for this task.
 * Attributes:
 * String csvFilePathName - Full o/s path of csv file - instantiated, getter, setter
 * ReviewsRow currentRow - Record structure that contains all data of current row. use inner class

 * Operations (implemented as public methods):-
 * void initialize - Opens Reviews CSV file and ready to iterate on rows
 * ReviewsRow nextRow - performs an iteration and returns next row, or null if end of file.
 * ReviewsRow getRow - returns current row.
 * void closeAll  - Closes reviews CSV file (and all associated streams etc).

 * Class to provide method to do analysis and return results.
 * Name: ReviewAnalysisEngine
 * Inner Class: AnalysisResults structure containing three arrays of the results.
 * Attributes:
 * final String CSV_PATH_NAME = <User Home>\Projects\ReviewAnalyzer; - see requirements
 * final int MAX_NUM_RESULTS = 1000; - see requirements
 * ReviewsCSV reviewsCSV - see above
 * AnalysisResults analysisResults - results of last analysis run using this engine

 * Operations:
 * void initialize - instantiates and initiates ReviewsCsv, AnalysisResults as well as any internal structures.
 * AnalysisResults  doAnalyze - performs analysis and returns results.


 * Time took to write above design: approx. 90 minutes.

 * Detailed design and issues:-

 * - Place of csv file in file system
 * - --------------------------------
 * - An assumption is made that the csv file is in the user home directory.
 * - For the purpose of the task, the path string value is hard coded.

 * - Parsing csv line
 * - ----------------
 * - A simple way of parsing a csv line is using split method on string with delimiter ","
 * - but this does not take into account the possibility of a comma in a quoted string.
 * - So could use more advanced regular expression for the split that ignores commas between quotes,
 * - but this is quite complicated and hard to maintain. Parsing the row using regular expression,
 * - taking into account two states "within quotes" and "without quotes" is easier to implement, but still a little tricky.
 * - The most advised way to do this is to use a third party open-source/freeware csv parsing package.
 * - For his task, the third party library super-csv ; licence http://super-csv.github.io/super-csv/license.html
 * - was downloaded and is being used. The class: org.supercsv.io.CsvBeanReader
 * - will be used.

 * - Dealing with ordering of columns for results
 * - ----------------------------------------------
 * - For each result type, the number of occurrences of each string value will be added into a separate hash map
 * - with the string value as the key and the number of times as the value.
 * - Each hashmap will be sorted by copying to a new corresponding tree set, with each element being a <string number> tuple
 * - and with a descending comparator by comparing the number.


 * - Parsing Text column in csv row
 * - ------------------------------------
 * - A regular expression will be used on string split method using sequences of non alphanumeric characters as delimiters.
 * - This is not perfect, but should obtain reasonable results.


 * - General
 * - ------=
 * - All output (also exception is written System output. System error stream is
 * - not explicitly used (although might be used by third party libraries).
 * - It is assumed that UTF8 character encoding is used.

 * Time took to write detailed design: approx. 45 minutes, including necessary research.
 * Net time taken to code original Java program approx. 3 hours
 *
 * Time to debug to level that there is reasonably plausible output: 30 mins
 * To debug further would need to take test file with a few lines only, and test border line scenarios.
 * Known limitation:- When counting words, a word such as " don't " is split into two words: "don" and "t".
 * This is because words are delimited by any non alphanumeric character - which includes "'" (apostrophe).
 *
 * - Port to Kotlin
 * ================
 * The Java version was ported to Kotlin, which involved starting to learn the Kotlin language, and then performing the port.
 * Of the Kotlin files, this class that contains the main program ReviewAnalyzer as well as ReviewAnalyzerEngine were ported using an automatic
 * transformation application that is available on IntelliJ IDEA 2017.2.1 . Minor changes had to be made.
 * ReviewsCVS class which is a wrapper to a Java library was ported manually.
 * The work took approximately two days, including obtaining a reasonable understanding of Kotlin to perform the task and then doing the
 * port, debugging and testing against the given input file.
 * Javadoc style comments was added to this version.
 *
 * @author David Ziants
 */

/**
 * The main object that runs the Review Analyzer Engine and then outputs the results.
 */

object ReviewAnalyzer {

    /**
     * Main program to do the run. this runs the Review Analyzer Engine (that reads the input file and calculates the statistics), and
     * then outputs the results.
     * Assumes input CSV file is in current director or in user home directory.
     *
     * @param args the command line arguments
     */
    @JvmStatic fun main(args: Array<String>) {
        val engine = ReviewAnalysisEngine()
        try {
            println("Starting Review Analysis.")
            val results = engine.doAnalyze()
            println()
            println("Here are the results:-")
            println()
            println("Top " + ReviewAnalysisEngine.MAX_NUM_RESULTS + " users who gave reviews:")
            System.out.printf("%-50s %-20s", "User Name", "Number of Reviews")
            println()
            var maxUserReviews = ReviewAnalysisEngine.MAX_NUM_RESULTS
            for (result in results.mostActiveUsers) {
                System.out.printf("%-50s %4d", result.str, result.numTimes)
                println()
                maxUserReviews--
                if (maxUserReviews <= 0) {
                    break // stop once reached maximum
                }
            }
            println()
            println("Top " + ReviewAnalysisEngine.MAX_NUM_RESULTS + " food items that have had reviews:")
            System.out.printf("%-50s %-20s", "Item Number", "Number of Reviews")
            println()
            for (result in results.mostCommentedItems) {
                System.out.printf("%-50s %4d", result.str, result.numTimes)
                println()
            }
            println()
            println("Top " + ReviewAnalysisEngine.MAX_NUM_RESULTS + " most used words that have appeared in reviews:")
            System.out.printf("%-50s %-20s", "Word", "Number of Times")
            println()
            for (result in results.mostUsedWords) {
                System.out.printf("%-50s %6d", result.str, result.numTimes)
                println()
            }
            println()
        } catch (t: Throwable) {
            println("An exception was thrown during processing.")
        } finally {
            try {
                engine.closeDown()
            } catch (t: Throwable) {
            }

        }
    }

}