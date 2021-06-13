package com.duyts.newspaper.ui.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.duyts.newspaper.databinding.EditTextDialogBinding;

import org.jetbrains.annotations.NotNull;

public class EditTextDialog extends AppCompatDialogFragment {

    private EditTextDialogBinding viewBinding;
    private final Callback cb;
    private Integer countDefault =  1;

    public EditTextDialog(Callback cb) {
        this.cb = cb;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));

        viewBinding = EditTextDialogBinding.inflate(getLayoutInflater());
        builder.setView(viewBinding.getRoot());
//        builder.setView(viewBinding.getRoot())
        viewBinding.countEditTextView.setText(countDefault.toString());

        viewBinding.addButton.setOnClickListener(v -> {
            String text = viewBinding.inputEditText.getText().toString();
            String countString = viewBinding.countEditTextView.getText().toString();
            countDefault = TextUtils.isDigitsOnly(countString) ? Integer.parseInt(countString) : 1;

            if (!TextUtils.isEmpty(text)) {
                cb.onSubmitInputLinkWithCount(text, countDefault);
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Empty input!", Toast.LENGTH_SHORT).show();
            }
        });
        viewBinding.randomButton.setOnClickListener(v -> {
            String countString = viewBinding.countEditTextView.getText().toString();
            countDefault = TextUtils.isDigitsOnly(countString) ? Integer.parseInt(countString) : 1;
            if (countDefault > 100000) {
                Toast.makeText(
                        getActivity(),
                        "Reach maximum number of links could be created (< 100000)!",
                        Toast.LENGTH_SHORT
                ).show();
                countDefault = 1;
            } else {
                cb.onRandomLinkWithCount(countDefault);
                dismiss();
            }

        });
        return builder.create();
    }

    public interface Callback {
        default void onSubmitInputLinkWithCount(String text, int count) {
        }

        default void onRandomLinkWithCount(int count) {
        }

    }
}
