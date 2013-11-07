import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class PigPen {

     public static char SET[] = { '0', '1', '2', '3' };
     public static int KEY_LENGTH = 7, count;
     public static List<String> KEYS = new ArrayList<String>(4096);
     public final String CIPHER_TEXT = "UHLEQFVTCPPWMPDOREERRIODIWLTUAREREHTAAFBAENOFERAEYHYOZIPVCYOSFKOYTESGHQPOTQNMDAPSTMGYIBOQRPFESTTLPAAFEEUHTSBFTNKTSVVSUIWTOGYTSTEFMHFROWOFZHERERPEUHTSBFTNKTSLHTUOTDLHTLPHSYQTGGVT";
    
     public static final int NUM_OF_WORDS_TRIGRAM = 676;
     public static long SCORE[] = new long[NUM_OF_WORDS_TRIGRAM];
     public static String PATTERN[] = new String[NUM_OF_WORDS_TRIGRAM];
     public static Hashtable<String, Long> TRIGRAM_TABLE = new Hashtable<String, Long>(NUM_OF_WORDS_TRIGRAM);
     public static long TOTAL_SCORE;
    
     // String alphabets[] = {"A", "L","B","C","D","E", "T","F","G","H","I","J","K","M","N","O","P","Q","R","S","U", "W","V","X","Y","Z"};
     // String alphabets[] = {"AL", "UW", "ET", "AL", "UW", "ET", "AL", "UW", "ET", "AL", "UW", "ET", "AL"};
     public Character alphabets[] = {'A', 'L','B','C','D','E', 'T','F','G','H','I','J','K','M','N','O','P','Q','R','S','U', 'W','V','X','Y','Z'};
     public static String ROTATE_KEY[] = {"????", "????", "????", "????", "????", "????", "?", "?"};
     String PAIR_UW="UW" ;
     String PAIR_LA="LA" ;
     String PAIR_ET="ET" ;

    
     synchronized public double computeScore(String decryptedText) {
          double score = 0, tempScore;
          double probability;
          String temp;
          for ( int i = 0; i < decryptedText.length() - 2; i++) {
               temp = decryptedText.substring(i, i + 2);
               if (TRIGRAM_TABLE.get(temp) == null)
                    tempScore = 0;
               else
                    tempScore = TRIGRAM_TABLE.get(temp);
              
               if (tempScore != 0) {
                    probability = 0;
                    probability = (double)tempScore/TOTAL_SCORE;
                    score += Math.log10(probability);
               }
          }
          return score;
     }
    
    
     synchronized public String decryptCipherText(int[] key) {
          int position = -1, newPosition = -1, temp;
          boolean charFound = false;
          StringBuilder output = new StringBuilder();
         
          // For EVERY char in Cipher Text
          for( int j = 0; j < CIPHER_TEXT.length(); j++) {
               charFound = false;
               char ch = CIPHER_TEXT.charAt(j);
               if ( j == 4 && !output.toString().equals("WHAT")) {
                    // OUTPUT is not what we expect. Return now
                    return null;
               }
               // don't run this loop for last two keys( they are one char long)
               for (int k = 0; k < PigPen.ROTATE_KEY.length - 2; k++) {
                    if ( PigPen.ROTATE_KEY[k].indexOf( ch ) != -1 ) {
                         charFound = true;
                         position = PigPen.ROTATE_KEY[k].indexOf(ch);
                         temp = key[ j % KEY_LENGTH ];
                         newPosition = position - temp;
                        
                         if ( newPosition >= 0) {
                              output.append(PigPen.ROTATE_KEY[k].charAt(newPosition));
                              break;
                         }
                         else {
                              output.append(PigPen.ROTATE_KEY[k].charAt(4 + newPosition));
                              break;
                         }
                    }
               }
              
               if (!charFound && ((PigPen.ROTATE_KEY[6].indexOf(ch) != -1) || (PigPen.ROTATE_KEY[7].indexOf(ch) != -1))) {
                    output.append(ch);
               }
          }
          return output.toString();
     }
    
    
     synchronized boolean checkForKeys(String pair) {
          for (int i = 0; i < 6; i++) {
               if (PigPen.ROTATE_KEY[i].indexOf(pair.charAt(0)) != -1 &&
                         PigPen.ROTATE_KEY[i].indexOf(pair.charAt(1)) != -1) {
                    return true;
               }
          }
          return false;
     }
    
     synchronized boolean fillValidateRotateKey(String alpha) {
          int i = 0, j = 0;
          for ( i = 0; i < 6; i ++) {
               PigPen.ROTATE_KEY[i] = alpha.substring(j, j + 4);
               j+= 4;
          }
         
          PigPen.ROTATE_KEY[6] = String.valueOf(alpha.charAt(24));
          PigPen.ROTATE_KEY[7] = String.valueOf(alpha.charAt(25));
         
          return checkForKeys(this.PAIR_UW) && checkForKeys(this.PAIR_LA) && checkForKeys(this.PAIR_ET);
     }
    
    
     public static void readScores() {
          int count = 0;
          System.out.println("Reading trigram file...");
          BufferedReader br = null;
          try {
               br = new BufferedReader(new FileReader("C:\\Users\\Niti\\workspace\\TestingProject\\english_bigrams.txt"));
               String line;
               while ((line = br.readLine()) != null) {
                    // process the line.
                    PATTERN[count] = line.split(" ")[0];
                    SCORE[count] = Long.parseLong(line.split(" ")[1]);
                    TRIGRAM_TABLE.put(line.split(" ")[0], Long.parseLong(line.split(" ")[1]));
                    TOTAL_SCORE += SCORE[count];
                    count++;
               }
               br.close();
          } catch (Exception e ) {
               e.printStackTrace();
          } finally {
               if (br != null ) {
                    try {
                         br.close();
                    } catch (IOException e) {
                         e.printStackTrace();
                    }
               }
          }
          System.out.println("Done trigram bigram file...");
     }

     // The method that prints all possible strings of length k. It is
     // mainly a wrapper over recursive function printAllKLengthRec()
     public static void generateAllKeys() {
          int n = SET.length;
          generateAllKeysRec(SET, "", n, KEY_LENGTH);
     }

     // The main recursive method to print all possible strings of length k
     public static void generateAllKeysRec(char set[], String prefix, int n, int k) {
          // Base case: k is 0, print prefix
          if (k == 0) {
               // System.out.println(count + " : " + prefix);
               // KEYS[count++] = prefix;
               if (prefix.indexOf('0') != 0 || prefix.indexOf('0') != 1)
                    KEYS.add(prefix);
               return;
          }
          // One by one add all characters from set and recursively
          // call for k equals to k-1
          for (int i = 0; i < n; ++i) {
               // Next character of input added
               String newPrefix = prefix + set[i];
               // k is decreased, because we have added a new character
               generateAllKeysRec(set, newPrefix, n, k - 1);
          }
     }


     // swaps array elements i and j
     synchronized public static void swap(char[] a, int i, int j) {
          char temp = a[i];
          a[i] = a[j];
          a[j] = temp;
     }

     // take as input an array of strings and rearrange them in random order
     synchronized public String shuffle(Character alpha[]) {
//          String alphaString = new String(alpha);
//         List<String> strings = new ArrayList<String>();
//         strings = Arrays.asList(alphaString);
//
//         Collections.shuffle(strings); 
//         StringBuilder sb = new StringBuilder(); 
//         for(String s : strings) { 
//             sb.append(s); 
//         }
//         return sb.toString(); 
          List<Character> shuffled = Arrays.asList(alpha);
          Collections.shuffle(shuffled);
          StringBuilder sb = new StringBuilder();
         for (int i = 0; i < shuffled.size(); i++) {
              sb.append(shuffled.get(i));
         }
          return sb.toString();
     }
}
