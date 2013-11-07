import java.util.Arrays;


public class PigPenRunner extends Thread {

public static double FINAL_SCORE = -9223372036854775808.9;
public static String FINAL_DECRYPTED_TEXT;
public static int[] FINAL_KEY;
public static String DECRYPTED_TEXT;

@Override
public synchronized void run() {
PigPen pigpen = new PigPen();
boolean decryptedTextFound = false;

while(true) {
String shuffledAlpha = pigpen.shuffle(pigpen.alphabets);
// pigpen.alphabets = shuffledAlpha.toCharArray();
// System.out.println(shuffledAlpha);
// shuffledAlpha = "UWJGALPBTEVXRQINCDFSKMOZHY";
boolean isValidRotateKey = pigpen.fillValidateRotateKey(shuffledAlpha);

int[] key = null;

if (isValidRotateKey) {
for ( int i = 0; i < PigPen.KEYS.size(); i++ ) {
key = getIntKeyFromString(PigPen.KEYS.get(i)); 
decryptedTextFound = decryptAndGetScore(key, pigpen);
}
swapRotateKeyAndGetScore( pigpen );
}
}
}

public static int[] getIntKeyFromString(String keyString ) {
int key[] = new int[ keyString.length() ];
for (int j = 0; j < keyString.length(); j++){
        key[j] = keyString.charAt(j) - '0';
    }
return key;
}

// Returns true if score is improved
public boolean decryptAndGetScore(int[] key, PigPen pigpen) {
double score;

// key = new int[]{3,0,1,1,0,0};
DECRYPTED_TEXT = pigpen.decryptCipherText( key );
//if (DECRYPTED_TEXT != null ) 
//System.out.println(DECRYPTED_TEXT);

if ( DECRYPTED_TEXT != null ) {
score = pigpen.computeScore(DECRYPTED_TEXT);
// System.out.println(score);
if ( score  > FINAL_SCORE) {
FINAL_SCORE = score;
FINAL_DECRYPTED_TEXT = DECRYPTED_TEXT;
FINAL_KEY = key;
System.out.println("New Score: " + score);
System.out.println("New Decrypted text: " + DECRYPTED_TEXT);
System.out.println("New Final key: " + Arrays.toString(key));
System.out.println("Rotation key: " + Arrays.toString(PigPen.ROTATE_KEY));
System.out.println("---------------------------------");

return true;
}
}
return false;
}

public char[] getRotateKeyChars() {
StringBuilder sb = new StringBuilder();
for (int i = 0; i < PigPen.ROTATE_KEY.length; i++) {
sb.append(PigPen.ROTATE_KEY[i]);
}
return sb.toString().toCharArray();
}


public void swapRotateKeyAndGetScore( PigPen pigpen) {
char[] chars = getRotateKeyChars();
for (int i = 0; i < chars.length ; i++ ) {
for (int j = 0; j < chars.length - 1; j++) {
PigPen.swap(chars, j, j + 1);

boolean isValidRotateKey = pigpen.fillValidateRotateKey(new String(chars));
double oldScore, newScore;
int[] bestScoreKey;
boolean scoreImproved = false;
int[] key = null;
if (isValidRotateKey) {
for ( int k = 0; k < PigPen.KEYS.size(); k++ ) {
key = getIntKeyFromString(PigPen.KEYS.get(k)); 
oldScore = FINAL_SCORE;
decryptAndGetScore(key, pigpen);
newScore = FINAL_SCORE;
if (newScore > oldScore) {
scoreImproved = true;
bestScoreKey = key;
}
}
// shuffleAndGetScore( pigpen );
}
if (!scoreImproved) {
PigPen.swap(chars, j, j + 1);
}
}
}
//for (int i = 0; i < PigPen.ROTATE_KEY.length - 1; i++) {
//
//for (int j = 0; j < PigPen.ROTATE_KEY[i].length(); j++) {
//// sb = new StringBuilder( PigPen.ROTATE_KEY[i]);
//for (int k = i + 1; k < PigPen.ROTATE_KEY.length; k++) {
//StringBuilder sb = new StringBuilder( PigPen.ROTATE_KEY[i]);
//char ch1 = sb.charAt(j);
//StringBuilder nextSb = new StringBuilder( PigPen.ROTATE_KEY[k]);
//char ch2 = nextSb.charAt(0);
//nextSb.setCharAt(0, ch1);
//sb.setCharAt(j, ch2);
//PigPen.ROTATE_KEY[k] = nextSb.toString();
//PigPen.ROTATE_KEY[i] = sb.toString();
//// System.out.println("i = " + i + " j = " + j + " k = " + k + "---------- Decrypting with : " + Arrays.toString(PigPen.ROTATE_KEY));
//
//boolean scoreImproved = decryptAndGetScore(key, pigpen);
//
//if (!scoreImproved) {
//// If the score is not improved, swap the characters to original index
//sb.setCharAt(j, ch1);
//nextSb.setCharAt(0, ch2);
//PigPen.ROTATE_KEY[k] = nextSb.toString();
//PigPen.ROTATE_KEY[i] = sb.toString();
//// System.out.println("Score didn't improve. New Keys: " + Arrays.toString(PigPen.ROTATE_KEY));
//}
//}
//}
//}
//return true;
}


public static void main(String[] args) {
// Populate PATTERN, SCORE and KEYS
PigPen.readScores();
PigPen.generateAllKeys();
// PigPen.filterKeys();

final int NUM_THREADS = 1;
PigPenRunner runner[] = new PigPenRunner[NUM_THREADS];
for (int i = 0; i < NUM_THREADS; i++) {
runner[i] = new PigPenRunner();
(runner[i]).start();
}
}
}