package com.example.druglikenesschecker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.druglikenesschecker.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {

            val name = binding.etDrugName.text.toString().trim()

            if (name.isNotEmpty()) {
                getDrugInfo(name)
            } else {
                Toast.makeText(this, "Enter drug name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDrugInfo(name: String) {

        Thread {
            try {
                val drug = URLEncoder.encode(name, "UTF-8")

                val url = URL(
                    "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/$drug/property/CanonicalSMILES,MolecularFormula,IUPACName/JSON"
                )

                val response = url.readText()

                val json = JSONObject(response)

                val properties = json.getJSONObject("PropertyTable")
                    .getJSONArray("Properties")
                    .getJSONObject(0)

                val formula = properties.optString("MolecularFormula")
                val iupac = properties.optString("IUPACName")
                val smiles = properties.optString("ConnectivitySMILES")

                runOnUiThread {

                    binding.tvFormula.text = "Formula: $formula"
                    binding.tvIUPAC.text = "IUPAC Name: $iupac"
                    binding.tvSMILES.text = "SMILES: $smiles"

                    Toast.makeText(
                        this,
                        "Drug found successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}