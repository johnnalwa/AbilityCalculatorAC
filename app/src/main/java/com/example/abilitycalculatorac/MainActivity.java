package com.example.abilitycalculatorac;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText netSalaryInput, basicSalaryInput, totalArrearsInput, grossSalaryInput;
    private Button calculateButton, clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        netSalaryInput = findViewById(R.id.netSalaryInput);
        basicSalaryInput = findViewById(R.id.basicSalaryInput);
        totalArrearsInput = findViewById(R.id.totalArrearsInput);
        grossSalaryInput = findViewById(R.id.grossSalaryInput);
        calculateButton = findViewById(R.id.calculateButton);
        clearButton = findViewById(R.id.clearButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateSalaryDetails();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });
    }

    private void calculateSalaryDetails() {
        try {
            double netSalary = Double.parseDouble(netSalaryInput.getText().toString());
            double basicSalary = Double.parseDouble(basicSalaryInput.getText().toString());
            double totalArrearsAndAllowances = Double.parseDouble(totalArrearsInput.getText().toString());
            double grossSalary = Double.parseDouble(grossSalaryInput.getText().toString());

            double paye = totalArrearsAndAllowances * (30.0 / 100);
            double houseLevy = totalArrearsAndAllowances * (1.5 / 100);
            double grossSalaryBeforeArrears = grossSalary - totalArrearsAndAllowances;
            double nhifDeduction = calculateNhifDeduction(grossSalary, grossSalaryBeforeArrears);

            double totalDeduction = paye + houseLevy + nhifDeduction;
            double deductionWithoutArrears = totalArrearsAndAllowances - totalDeduction;
            double netSalaryAfterDeductions = netSalary - deductionWithoutArrears;

            double ability = netSalaryAfterDeductions - (1.0 / 3.0 * basicSalary);

            String result = "PAYE: " + paye + "\n" +
                    "House Levy: " + houseLevy + "\n" +
                    "NHIF Deduction: " + nhifDeduction + "\n" +
                    "Total Deduction: " + totalDeduction + "\n" +
                    "Deduction Without Arrears: " + deductionWithoutArrears + "\n" +
                    "Net Salary After Deductions: " + netSalaryAfterDeductions + "\n" +
                    "Calculated Ability: " + ability;

            showResultsPopup(result);
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Invalid input. Please enter numerical values.", Toast.LENGTH_LONG).show();
        }
    }

    private double calculateNhifDeduction(double grossSalary, double grossSalaryBeforeArrears) {
        double nhifContributionGrossSalary = getNhifContribution(grossSalary);
        double nhifContributionGrossSalaryBeforeArrears = getNhifContribution(grossSalaryBeforeArrears);
        return nhifContributionGrossSalary - nhifContributionGrossSalaryBeforeArrears;
    }

    private double getNhifContribution(double grossSalary) {
        double[][] nhifContributions = {
                {5999, 150.00},
                {7999, 300.00},
                {11999, 400.00},
                {14999, 500.00},
                {19999, 600.00},
                {24999, 750.00},
                {29999, 850.00},
                {34999, 900.00},
                {39999, 950.00},
                {44999, 1000.00},
                {49999, 1100.00},
                {59999, 1200.00},
                {69999, 1300.00},
                {79999, 1400.00},
                {89999, 1500.00},
                {99999, 1600.00},
                {Double.MAX_VALUE, 1700.00}
        };

        for (double[] nhif : nhifContributions) {
            if (grossSalary <= nhif[0]) {
                return nhif[1];
            }
        }
        return 500.00;  // Default value for self-employed special
    }

    private void showResultsPopup(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculation Results")
                .setMessage(result)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    private void clearFields() {
        netSalaryInput.setText("");
        basicSalaryInput.setText("");
        totalArrearsInput.setText("");
        grossSalaryInput.setText("");
    }
}
