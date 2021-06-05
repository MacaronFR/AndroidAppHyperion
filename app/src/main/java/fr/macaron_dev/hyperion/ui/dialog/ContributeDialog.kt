package fr.macaron_dev.hyperion.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import fr.macaron_dev.hyperion.R
import java.lang.ClassCastException
import java.lang.IllegalStateException

class ContributeDialog: DialogFragment() {

    private lateinit var listener: ContributeDialogListener

    interface ContributeDialogListener{
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setTitle(R.string.contribute)
                .setView(inflater.inflate(R.layout.contribute_layout, null))
                .setPositiveButton(R.string.contribute){ _, _ -> listener.onDialogPositiveClick(this)}
                .setNegativeButton(R.string.cancel){_, _ -> listener.onDialogNegativeClick(this)}
            builder.create()
        } ?: throw  IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as ContributeDialogListener
        }catch (e: ClassCastException){
            throw ClassCastException(("$context must implement DisconnectDialogListener"))
        }
    }

}