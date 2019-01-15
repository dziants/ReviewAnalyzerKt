/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dz.analyzers


import dz.analyzers.ReviewsCSV
import sun.misc.Regexp
import java.util.HashMap
import java.util.TreeSet

/**

 * @author David
 */
class ReviewAnalysisEngine {

    class AnalysisResults {

        internal class StringNumTimesPair(val str: String, val numTimes: Int) : Comparable<StringNumTimesPair> {

            override fun compareTo(pair: StringNumTimesPair): Int {
                // return (numTimes < pair.numTimes) ? -1 : ((numTimes > pair.numTimes) ? 1 : 0);
                // Force descending ordering
                return if (numTimes < pair.numTimes) 1 else if (numTimes > pair.numTimes) -1 else 0
            }

        }

        internal var mostActiveUsers = TreeSet<StringNumTimesPair>()
        internal var mostCommentedItems = TreeSet<StringNumTimesPair>()
        internal var mostUsedWords = TreeSet<StringNumTimesPair>()
    }

    internal var reviewsCSV = ReviewsCSV(CSV_PATH_NAME1, CSV_PATH_NAME2);
    internal var analysisResults = AnalysisResults()

    internal var activeUsersMap: MutableMap<String, Int> = HashMap(2000)
    internal var mostCommentedItemsMap: MutableMap<String, Int> = HashMap(2000)
    internal var mostUsedWordsMap: MutableMap<String, Int> = HashMap(5000)
    /**
     * Analyses CSV file as reading it, and accumalates statistical data.
     */
    @Throws(Throwable::class)
    fun doAnalyze(): AnalysisResults {
        // Do initialization of CSV wrapper
        reviewsCSV.initialize();
        // Iterate on reviews
        val wrdSplt = Regex("\\W+")
        try {
            do {
                // Break when nextRow returns null. (Null value used as the underligning library for CSV is Java based.
                val reviewsRow: ReviewsCSV.ReviewsRow = reviewsCSV!!.nextRow() ?: break

                // Accumalate statistics
                addNumTimesToMap(activeUsersMap, reviewsRow.profileName)
                addNumTimesToMap(mostCommentedItemsMap, reviewsRow.productId)

                // Obtain words in text by splitting on one or more  non-alphanumeric characters, and for each word, if word on hashmap then
                // increment count value by 1, otherwise put new value 1
                val wordList = reviewsRow.text.split(wrdSplt)
                // For each word in wordlist, add to hashmap
                for (wrd in wordList) {
                    //provided not blank (should not be because regular expression should not include blanks)
                    if ("" != wrd.trim({ it <= ' ' })) {
                        addNumTimesToMap(mostUsedWordsMap, wrd)
                    }
                }
            } while (true)
        } catch (t: Throwable) {
            throw t
        }

        analysisResults = makeAnalysisResults()
        return analysisResults
    }

    /**
     * Creates analysis results from accumalated statistics
     * @return Analysis results object
     */
    fun makeAnalysisResults(): AnalysisResults {
        analysisResults.mostActiveUsers = copyFromMap(activeUsersMap)
        analysisResults.mostCommentedItems = copyFromMap(mostCommentedItemsMap)
        analysisResults.mostUsedWords = copyFromMap(mostUsedWordsMap)
        return analysisResults
    }

    /**
     * Close down all resources on completion of execution.
     */
    @Throws(Throwable::class)
    fun closeDown() {
        reviewsCSV.closeAll()
    }

    private fun copyFromMap(map: Map<String, Int>): TreeSet<AnalysisResults.StringNumTimesPair> {
        val treeSet = TreeSet<AnalysisResults.StringNumTimesPair>()
        // For each element in map, adds to TreeSet that contains ordered results
        map.forEach { str, num -> treeSet.add(AnalysisResults.StringNumTimesPair(str, num)) }
        return treeSet
    }

    private fun addNumTimesToMap(map: MutableMap<String, Int>, str: String) {
        // If string on map then increment count value by 1, otherwise put new entry with string and value 1
        if (map.containsKey(str)) {
            map.put(str, map[str]!! + 1)
        } else {
            map.put(str, 1)
        }
    }

    /**
     * Contains constants used for run.
     */
    companion object {
        val CSV_PATH_NAME1 = System.getProperty("user.home") + "\\Reviews.csv"
        val CSV_PATH_NAME2 = "." + "\\Reviews.csv"
        val MAX_NUM_RESULTS = 1000
    }

}
