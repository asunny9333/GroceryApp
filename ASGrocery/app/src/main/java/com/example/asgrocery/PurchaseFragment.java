package com.example.asgrocery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asgrocery.CommonUtils;
import com.example.asgrocery.DataBaseHelper;
import com.example.asgrocery.databinding.FragmentPurchaseBinding;
import com.example.asgrocery.StockItemData;

public class PurchaseFragment extends Fragment {

    DataBaseHelper dataBaseHelper;
    private FragmentPurchaseBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPurchaseBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataBaseHelper = new DataBaseHelper(getContext());

        binding.btnCancel.setOnClickListener(view1 -> {
            requireActivity().onBackPressed();
        });

        binding.edtDop.setOnClickListener(view1 -> {
            binding.edtDop.setOnClickListener(v -> CommonUtils.openDatePicker(binding.edtDop, requireActivity().getSupportFragmentManager()));
        });

        binding.btnSave.setOnClickListener(view1 -> {
            if (isValid()) {
                String itemCode = binding.edtItemCode.getText().toString();
                String qtySold = binding.edtQtySold.getText().toString();
                String dop = binding.edtDop.getText().toString();

                checkItemExist(itemCode, qtySold, dop);

            }
        });


    }

    private void checkItemExist(String itemCode, String qtySold, String dop) {

        StockItemData stockItemData = dataBaseHelper.getStockItemById(Integer.parseInt(itemCode));
        if (stockItemData != null) {
            // The item with the given ID was found, and you can access its properties using stockItem.
            addPurchaseData(itemCode, qtySold, dop);

        } else {
            // The item with the given ID was not found.
            CommonUtils.showMaterialDialog(getContext(), "Item Not Found", (dialogInterface, i) -> {
            });
        }

    }

    private void addPurchaseData(String itemCode, String qtySold, String dop) {

        // Add a new record to the sales table
        long addNewItem = dataBaseHelper.insertPurchaseItem(Integer.parseInt(itemCode), Integer.parseInt(qtySold), dop);

        if (addNewItem != -1) {
            // Successfully inserted the record
            CommonUtils.showMaterialDialog(getContext(), "Item Purchased Successfully.", (dialogInterface, i) -> {
                binding.edtItemCode.setText("");
                binding.edtQtySold.setText("");
                binding.edtDop.setText("");
            });

            //updating the stock table
            dataBaseHelper.updateQtyStock(Integer.parseInt(itemCode), Integer.parseInt(qtySold));

        } else {
            // Failed to insert the record
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    protected boolean isValid() {
        String itemCode = binding.edtItemCode.getText().toString();
        String qtySold = binding.edtQtySold.getText().toString();
        String dop = binding.edtDop.getText().toString();
        if (itemCode.length() == 0 || qtySold.length() == 0 || dop.length() == 0) {
            CommonUtils.showMaterialDialog(getContext(), "You should fill all the blanks!", (dialogInterface, i) -> {
            });
            return false;
        }
        return true;
    }

}
