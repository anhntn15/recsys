/**
 * Package implements logic recommendation of content-based-recommender (V2)
 * - assign weight for each item in history item list, based on 'click2call action' or 'time on page'
 * - calculate similarity between candidate item & each item in history by get similarity of each feature
 * - calculate similarity between candidate item & history by multiply 'weight' vs 'similarity'
 */
package similarity;