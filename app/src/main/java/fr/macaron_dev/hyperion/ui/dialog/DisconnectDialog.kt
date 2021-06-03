package fr.macaron_dev.hyperion.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import fr.macaron_dev.hyperion.R
import java.lang.ClassCastException
import java.lang.IllegalStateException

class DisconnectDialog: DialogFragment() {

    private lateinit var listener: DisconnectDialogListener

    interface DisconnectDialogListener{
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.disconnect)
                .setPositiveButton("Disconnect") { _, _ -> listener.onDialogPositiveClick(this) }
                .setNegativeButton("Cancel") { _, _ -> listener.onDialogNegativeClick(this) }
            builder.create()
        } ?: throw IllegalStateException("Activity Cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            listener = context as DisconnectDialogListener
        }catch (e: ClassCastException){
            throw ClassCastException(("$context must implement DisconnectDialogListener"))
        }
    }
}