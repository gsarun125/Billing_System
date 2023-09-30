package com.bill.billingsystem.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.bill.billingsystem.R
import com.bill.billingsystem.databinding.ActivityHistorysBinding

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistorysActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistorysBinding

    var From_time:Long = 0
    var To_time:Long=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historys)
        binding = ActivityHistorysBinding.inflate(layoutInflater)
        setContentView(binding.root)
        insert()

    }
    @SuppressLint("SuspiciousIndentation")
    fun insert(){
        binding.Fromdate.editText?.setOnClickListener {


            val calendarConstraintBuilder = CalendarConstraints.Builder()
            calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now())
            val materialDatePickerBuilder: MaterialDatePicker.Builder<*> =
                MaterialDatePicker.Builder.datePicker()
            materialDatePickerBuilder.setTitleText("SELECT A DATE")
            materialDatePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build())
            val materialDatePicker = materialDatePickerBuilder.build()

            materialDatePicker.addOnPositiveButtonClickListener {

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.format(it)

                //date=  materialDatePicker.getHeaderText().format(0)
                //println(date)

                val sdf1 = SimpleDateFormat("dd", Locale.getDefault())
                val sdf2 = SimpleDateFormat("MM", Locale.getDefault())
                val sdf3 = SimpleDateFormat("yyyy", Locale.getDefault())

              val  D=  sdf1.format(it).toInt()
               val M=  sdf2.format(it).toInt()
              val  Y=  sdf3.format(it).toInt()
                val calender= Calendar.getInstance()
                calender.set(Y,M-1,D,0,0)
                From_time = calender.getTimeInMillis()


                binding.Fromdate.editText?.setText(date)

            }

            materialDatePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }
        binding.Todate.editText?.setOnClickListener {


            val calendarConstraintBuilder = CalendarConstraints.Builder()
            calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now())
            val materialDatePickerBuilder: MaterialDatePicker.Builder<*> =
                MaterialDatePicker.Builder.datePicker()
            materialDatePickerBuilder.setTitleText("SELECT A DATE")
            materialDatePickerBuilder.setCalendarConstraints(calendarConstraintBuilder.build())
            val materialDatePicker = materialDatePickerBuilder.build()

            materialDatePicker.addOnPositiveButtonClickListener {

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.format(it)

                //date=  materialDatePicker.getHeaderText().format(0)
                //println(date)

                val sdf1 = SimpleDateFormat("dd", Locale.getDefault())
                val sdf2 = SimpleDateFormat("MM", Locale.getDefault())
                val sdf3 = SimpleDateFormat("yyyy", Locale.getDefault())

                val  D=  sdf1.format(it).toInt()
                val M=  sdf2.format(it).toInt()
                val  Y=  sdf3.format(it).toInt()

                val calender= Calendar.getInstance()
                calender.set(Y,M-1,D,23,59)
                To_time = calender.getTimeInMillis()



                binding.Todate.editText?.setText(date)

            }

            materialDatePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.view.setOnClickListener {
            if (CheckAllFields()){
            if(To_time>=From_time){
                val i = Intent(this, HisPdfviewActivity::class.java)
                i.putExtra("to_time",To_time)
                i.putExtra("from",From_time)
                startActivity(i)

            }
            else{
                Toast.makeText(this,"invalid Date",Toast.LENGTH_SHORT).show()
            }

        }
        }


    }
    private fun CheckAllFields(): Boolean{
        if (binding.Fromdate.editText?.text?.length == 0){
            binding.Fromdate.error = "From Date is required"
            binding.Fromdate.isFocusable = true
            binding.Fromdate.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.Fromdate.editText, InputMethodManager.SHOW_IMPLICIT)
            return false
        }
        if (binding.Todate.editText?.text?.length == 0){
            binding.Todate.error = "To Date is required"
            binding.Todate.isFocusable = true
            binding.Todate.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.Fromdate.editText, InputMethodManager.SHOW_IMPLICIT)
            return false
        }
        return true
    }

}