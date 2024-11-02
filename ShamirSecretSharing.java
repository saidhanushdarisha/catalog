import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        // Read both test cases
        String[] testFiles = {"testcase1.json", "testcase2.json"};
        for (String testFile : testFiles) {
            try {
                JSONObject testData = (JSONObject) new JSONParser().parse(new FileReader(testFile));
                System.out.println("Constant term (c) for " + testFile + ": " + findConstantTerm(testData));
            } catch (IOException | ParseException e) {
                System.out.println("Error reading or parsing JSON file: " + e.getMessage());
            }
        }
    }

    private static BigInteger findConstantTerm(JSONObject jsonObject) {
        // Read 'keys' to get n and k
        JSONObject keys = (JSONObject) jsonObject.get("keys");
        int n = ((Long) keys.get("n")).intValue();
        int k = ((Long) keys.get("k")).intValue();

        // Read and decode each root (x, y)
        Map<Integer, BigInteger> roots = new HashMap<>();
        for (int i = 1; i <= n; i++) {
            JSONObject root = (JSONObject) jsonObject.get(String.valueOf(i));
            if (root != null) {
                int x = i;
                int base = Integer.parseInt((String) root.get("base"));
                BigInteger y = new BigInteger((String) root.get("value"), base);
                roots.put(x, y);
            }
        }

        // Use Lagrange interpolation to find constant term
        return lagrangeInterpolation(roots, k);
    }

    private static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;

        // Iterate over each point for the Lagrange interpolation
        for (Map.Entry<Integer, BigInteger> entry : points.entrySet()) {
            int xi = entry.getKey();
            BigInteger yi = entry.getValue();
            BigInteger term = yi;

            // Calculate the Lagrange basis polynomial L_i(x) for this point
            for (Map.Entry<Integer, BigInteger> innerEntry : points.entrySet()) {
                int xj = innerEntry.getKey();
                if (xj != xi) {
                    term = term.multiply(BigInteger.valueOf(-xj)).divide(BigInteger.valueOf(xi - xj));
                }
            }

            // Add the term to the result for each basis polynomial
            result = result.add(term);
        }

        return result;
    }
}
