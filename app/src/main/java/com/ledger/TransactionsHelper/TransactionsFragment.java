package com.ledger.TransactionsHelper;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ledger.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class TransactionsFragment extends Fragment implements SortTransactionsDialog.OnDatesSelected {

    RecyclerView transactions;
    FirebaseFirestore db;
    ArrayList<TransactionsModel> transactionsModelArrayList;
    ArrayList<TransactionsModel> transactionsModelArrayList2;

    String company;
    String sales;
    String id;

    SearchView searchView;
    TransactionsAdapter transactionsAdapter;

    ImageButton sort;
    ImageButton download;
    ImageButton share;

    String startDateExisting;
    String endDateExisting;

    Date startDateD;
    Date endDateD;
    Date dateD;

    String creditAmount;
    String debitAmount;

    int currentDebit;
    int currentCredit;

    int openingCreditAmount;
    int openingDebitAmount;
    int openingBalance;

    int outStandingAmount;

    TextView currentC;
    TextView currentD;
    TextView outstanding;
    TextView outStandingCredit;

    TextView openingCredit;
    TextView openingDebit;

    String finalDateS;

    String dealerName;
    String startingDate;
    String endingDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);

        currentC = view.findViewById(R.id.currentCredit);
        currentD = view.findViewById(R.id.currentDebit);
        outstanding = view.findViewById(R.id.outStandingTotal);
        openingDebit = view.findViewById(R.id.openingBalanceDebit);
        openingCredit = view.findViewById(R.id.openingBalanceCredit);
        outStandingCredit = view.findViewById(R.id.outStandingCredit);

        sort.setVisibility(View.VISIBLE);
        download.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortTransactionsDialog sortTransactionsDialog = new SortTransactionsDialog(startDateExisting,endDateExisting);
                sortTransactionsDialog.setTargetFragment(TransactionsFragment.this, 1);
                sortTransactionsDialog.show(getActivity().getSupportFragmentManager(), "Sort transactions");
            }
        });


        Bundle bundle = getArguments();
        company = bundle.getString("company");
        sales = bundle.getString("sales");
        id = bundle.getString("userId");

        Log.i("company",company);
        Log.i("sales",sales);
        Log.i("userId",id);

        transactions = view.findViewById(R.id.transactions_recycler);
        db = FirebaseFirestore.getInstance();
        transactionsModelArrayList = new ArrayList<>();
        transactionsModelArrayList2 = new ArrayList<>();

        transactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactions.setHasFixedSize(true);

        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getTransactions();
            }
        });

        searchView = view.findViewById(R.id.transactionSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPDFFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = null;

                try {
                    file = createPDFFileAndShare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", file);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(share, "Share"));

            }
        });

        return view;
    }

    private void createPDFFile() throws IOException {

        String path = Environment.getExternalStorageDirectory() + File.separator + "ledger.pdf";
        File file = new File(path);

        if(!file.exists()){
            file.createNewFile();
        }

        try{
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            BaseFont fontName = BaseFont.createFont("assets/fonts/Roboto-Black.ttf","UTF-8" , BaseFont.EMBEDDED);
            BaseFont font1 = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf","UTF-8" , BaseFont.EMBEDDED);

            Font titleFont = new Font(fontName, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font contactFont = new Font(font1, 12.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerFont = new Font(fontName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerAddressFont = new Font(font1, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont = new Font(font1, 16.0f, Font.NORMAL, BaseColor.BLACK);

            addNewItem(document, "21 ST CENTURY BUSINESS SYNDICATE", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "Contact : 0612-2325412,9334120345", Element.ALIGN_CENTER, contactFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Dealer",Element.ALIGN_CENTER,dealerFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Dealer Address",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Ledger Account",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "1/08/2020 - 10/09/2020",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            addTable(document, transactionsModelArrayList);

            float[] columnWidths = {80f,100f,80f,100f,100f};

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Paragraph(new Chunk("Date", titleFont)));
            cell1.setBorderColor(BaseColor.WHITE);
            cell1.setFixedHeight(50f);

            PdfPCell cell2 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("particular", titleFont))));
            cell2.setBorderColor(BaseColor.WHITE);
            cell2.setFixedHeight(50f);

            PdfPCell cell3 = new PdfPCell(new Paragraph( new Paragraph(new Chunk("Vch No.", titleFont))));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorderColor(BaseColor.WHITE);
            cell3.setFixedHeight(50f);

            PdfPCell cell4 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Debit", titleFont))));
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setBorderColor(BaseColor.WHITE);
            cell4.setFixedHeight(50f);

            PdfPCell cell5 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Credit", titleFont))));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorderColor(BaseColor.WHITE);
            cell5.setFixedHeight(50f);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            PdfPCell cell11 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(finalDateS, transactionFont))));
            PdfPCell cell12 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Opening Balance", transactionFont))));
            PdfPCell cell13 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell14;
            PdfPCell cell15;
            if(openingBalance>=0){
                 cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
                 cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
            }
            else {
                 cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
                 cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            }

            cell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell15.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell11.setBorderColor(BaseColor.WHITE);
            cell12.setBorderColor(BaseColor.WHITE);
            cell13.setBorderColor(BaseColor.WHITE);
            cell14.setBorderColor(BaseColor.WHITE);
            cell15.setBorderColor(BaseColor.WHITE);

            table.addCell(cell11);
            table.addCell(cell12);
            table.addCell(cell13);
            table.addCell(cell14);
            table.addCell(cell15);


            for(int i = 0; i<transactionsModelArrayList.size();i++){

                PdfPCell cell6 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDate(), transactionFont))));
                PdfPCell cell7 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getParticular(), transactionFont))));
                PdfPCell cell8 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getVoucher(), transactionFont))));
                PdfPCell cell9 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDebit(), transactionFont))));
                PdfPCell cell10 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getCredit(), transactionFont))));

                cell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);

                cell6.setFixedHeight(30f);
                cell7.setFixedHeight(30f);
                cell8.setFixedHeight(30f);
                cell9.setFixedHeight(30f);
                cell10.setFixedHeight(30f);

                cell6.setBorderColor(BaseColor.WHITE);
                cell7.setBorderColor(BaseColor.WHITE);
                cell8.setBorderColor(BaseColor.WHITE);
                cell9.setBorderColor(BaseColor.WHITE);
                cell10.setBorderColor(BaseColor.WHITE);

                table.addCell(new PdfPCell(cell6));
                table.addCell(new PdfPCell(cell7));
                table.addCell(new PdfPCell(cell8));
                table.addCell(new PdfPCell(cell9));
                table.addCell(new PdfPCell(cell10));
                table.setSpacingBefore(10f);

            }


            document.add(table);

            document.close();

            Toast.makeText(getContext(),"Pdf generated",Toast.LENGTH_SHORT).show();
        }catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private File createPDFFileAndShare() throws IOException {

        String path = Environment.getExternalStorageDirectory() + File.separator + "ledger.pdf";
        File file = new File(path);

        if(!file.exists()){
            file.createNewFile();
        }

        try{
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            BaseFont fontName = BaseFont.createFont("assets/fonts/Roboto-Black.ttf","UTF-8" , BaseFont.EMBEDDED);
            BaseFont font1 = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf","UTF-8" , BaseFont.EMBEDDED);

            Font titleFont = new Font(fontName, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font contactFont = new Font(font1, 12.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerFont = new Font(fontName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerAddressFont = new Font(font1, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont = new Font(font1, 16.0f, Font.NORMAL, BaseColor.BLACK);

            addNewItem(document, "21 ST CENTURY BUSINESS SYNDICATE", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "Contact : 0612-2325412,9334120345", Element.ALIGN_CENTER, contactFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Dealer",Element.ALIGN_CENTER,dealerFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Dealer Address",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Ledger Account",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "1/08/2020 - 10/09/2020",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            addTable(document, transactionsModelArrayList);

            float[] columnWidths = {80f,100f,80f,100f,100f};

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Paragraph(new Chunk("Date", titleFont)));
            cell1.setBorderColor(BaseColor.WHITE);
            cell1.setFixedHeight(50f);

            PdfPCell cell2 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("particular", titleFont))));
            cell2.setBorderColor(BaseColor.WHITE);
            cell2.setFixedHeight(50f);

            PdfPCell cell3 = new PdfPCell(new Paragraph( new Paragraph(new Chunk("Vch No.", titleFont))));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorderColor(BaseColor.WHITE);
            cell3.setFixedHeight(50f);

            PdfPCell cell4 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Debit", titleFont))));
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setBorderColor(BaseColor.WHITE);
            cell4.setFixedHeight(50f);

            PdfPCell cell5 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Credit", titleFont))));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorderColor(BaseColor.WHITE);
            cell5.setFixedHeight(50f);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            PdfPCell cell11 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(finalDateS, transactionFont))));
            PdfPCell cell12 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Opening Balance", transactionFont))));
            PdfPCell cell13 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell14;
            PdfPCell cell15;
            if(openingBalance>=0){
                cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
                cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
            }
            else {
                cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
                cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            }

            cell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell15.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell11.setBorderColor(BaseColor.WHITE);
            cell12.setBorderColor(BaseColor.WHITE);
            cell13.setBorderColor(BaseColor.WHITE);
            cell14.setBorderColor(BaseColor.WHITE);
            cell15.setBorderColor(BaseColor.WHITE);

            table.addCell(cell11);
            table.addCell(cell12);
            table.addCell(cell13);
            table.addCell(cell14);
            table.addCell(cell15);


            for(int i = 0; i<transactionsModelArrayList.size();i++){

                PdfPCell cell6 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDate(), transactionFont))));
                PdfPCell cell7 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getParticular(), transactionFont))));
                PdfPCell cell8 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getVoucher(), transactionFont))));
                PdfPCell cell9 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDebit(), transactionFont))));
                PdfPCell cell10 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getCredit(), transactionFont))));

                cell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);

                cell6.setFixedHeight(30f);
                cell7.setFixedHeight(30f);
                cell8.setFixedHeight(30f);
                cell9.setFixedHeight(30f);
                cell10.setFixedHeight(30f);

                cell6.setBorderColor(BaseColor.WHITE);
                cell7.setBorderColor(BaseColor.WHITE);
                cell8.setBorderColor(BaseColor.WHITE);
                cell9.setBorderColor(BaseColor.WHITE);
                cell10.setBorderColor(BaseColor.WHITE);

                table.addCell(new PdfPCell(cell6));
                table.addCell(new PdfPCell(cell7));
                table.addCell(new PdfPCell(cell8));
                table.addCell(new PdfPCell(cell9));
                table.addCell(new PdfPCell(cell10));
                table.setSpacingBefore(10f);

            }


            document.add(table);

            document.close();

            Toast.makeText(getContext(),"Pdf generated",Toast.LENGTH_SHORT).show();
        }catch (DocumentException e) {
            e.printStackTrace();
        }

        return  file;
    }

    private void addTable(Document document, ArrayList<TransactionsModel> transactionsModelArrayList) throws DocumentException {


    }
    private void addNewItem(Document document, String text, int alignCenter, Font font) throws DocumentException {

        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(alignCenter);
        document.add(paragraph);
    }
    private void getTransactions(){

        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                transactionsModelArrayList.clear();
                currentDebit = 0;
                currentCredit = 0;
                openingCreditAmount = 0;
                openingDebitAmount = 0;
                openingBalance = 0;
                outStandingAmount = 0;

                currentC.setText("");
                currentD.setText("");
                outstanding.setText("");
                outStandingCredit.setText("");
                openingCredit.setText("");
                openingDebit.setText("");

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    TransactionsModel transactionsModel = new TransactionsModel();

                    if(documentSnapshot.getString("type").equals("Credit")){
                        creditAmount = documentSnapshot.getString("amount");
                        debitAmount = "0";

                        transactionsModel.setCredit(documentSnapshot.getString("amount"));
                        transactionsModel.setDebit("");
                    }
                    else {
                        transactionsModel.setDebit(documentSnapshot.getString("amount"));
                        transactionsModel.setCredit("");

                        creditAmount = "0";
                        debitAmount = documentSnapshot.getString("amount");

                    }

                    DateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
                    Date date = new Date();
                    finalDateS = "01/"+dateFormat.format(date);

                    Date finalDate = null;
                    Date currentDate = null;

                    try {
                        finalDate = new SimpleDateFormat("dd/MM/yyyy").parse(finalDateS);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                         currentDate = new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(!currentDate.before(finalDate)){
                        currentDebit += Integer.parseInt(debitAmount);
                        currentCredit += Integer.parseInt(creditAmount);
                    }

                    if(!currentDate.after(finalDate)){

                        openingCreditAmount += Integer.parseInt(creditAmount);
                        openingDebitAmount += Integer.parseInt(debitAmount);

                    }

                    transactionsModel.setDate(documentSnapshot.getString("date"));
                    transactionsModel.setParticular(documentSnapshot.getString("particular"));
                    transactionsModel.setVoucher(documentSnapshot.getString("voucher"));
                    try {
                        transactionsModel.setDateD(new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    transactionsModelArrayList.add(transactionsModel);
                }

                openingBalance = openingCreditAmount - openingDebitAmount;
                outStandingAmount = currentCredit - currentDebit+openingBalance;

                if(openingBalance<0){
                    openingDebit.setText(String.valueOf(Math.abs(openingBalance)));
                }
                else {
                    openingCredit.setText(String.valueOf(Math.abs(openingBalance)));
                }

                if(outStandingAmount>0){
                    outStandingCredit.setText(String.valueOf(Math.abs(outStandingAmount)));
                }
                else {
                    outstanding.setText(String.valueOf(Math.abs(outStandingAmount)));
                }

                currentC.setText(String.valueOf(Math.abs(currentCredit)));
                currentD.setText(String.valueOf(Math.abs(currentDebit)));

                Comparator c = Collections.reverseOrder();
                Collections.sort(transactionsModelArrayList,c);

                transactionsAdapter = new TransactionsAdapter(getContext(), transactionsModelArrayList);
                transactions.setAdapter(transactionsAdapter);


            }
        });
    }

    private void getSortedTransactions(final String start, final String end){

        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                transactionsModelArrayList.clear();
                currentDebit = 0;
                currentCredit = 0;
                openingCreditAmount = 0;
                openingDebitAmount = 0;
                openingBalance = 0;
                outStandingAmount = 0;

                currentC.setText("");
                currentD.setText("");
                outstanding.setText("");
                outStandingCredit.setText("");
                openingCredit.setText("");
                openingDebit.setText("");

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    TransactionsModel transactionsModel = new TransactionsModel();

                    try {
                        startDateD = new SimpleDateFormat("dd/MM/yyyy").parse(start);
                        startDateD.getMonth();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        endDateD = new SimpleDateFormat("dd/MM/yyyy").parse(end);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        dateD = new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(documentSnapshot.getString("type").equals("Credit")){
                        transactionsModel.setCredit(documentSnapshot.getString("amount"));
                        transactionsModel.setDebit("");

                        creditAmount = documentSnapshot.getString("amount");
                        debitAmount = "0";

                    }
                    else {
                        transactionsModel.setDebit(documentSnapshot.getString("amount"));
                        transactionsModel.setCredit("");

                        creditAmount = "0";
                        debitAmount = documentSnapshot.getString("amount");
                    }

                    try {
                        transactionsModel.setDateD(new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    finalDateS = String.valueOf(startDateD);

                    transactionsModel.setDate(documentSnapshot.getString("date"));
                    transactionsModel.setParticular(documentSnapshot.getString("particular"));
                    transactionsModel.setVoucher(documentSnapshot.getString("voucher"));

                    if(!dateD.before(startDateD)){
                        currentDebit += Integer.parseInt(debitAmount);
                        currentCredit += Integer.parseInt(creditAmount);
                    }

                    if(dateD.before(startDateD)){
                        openingCreditAmount += Integer.parseInt(creditAmount);
                        openingDebitAmount += Integer.parseInt(debitAmount);
                    }

                    if(!dateD.before(startDateD)&& !dateD.after(endDateD)){
                        transactionsModelArrayList.add(transactionsModel);
                    }
                }

                openingBalance = openingCreditAmount - openingDebitAmount;
                outStandingAmount = currentCredit - currentDebit+openingBalance;

                if(openingBalance<0){
                    openingDebit.setText(String.valueOf(Math.abs(openingBalance)));
                }
                else {
                    openingCredit.setText(String.valueOf(Math.abs(openingBalance)));
                }

                if(outStandingAmount>0){
                    outStandingCredit.setText(String.valueOf(Math.abs(outStandingAmount)));
                }
                else {
                    outstanding.setText(String.valueOf(Math.abs(outStandingAmount)));
                }

                currentC.setText(String.valueOf(Math.abs(currentCredit)));
                currentD.setText(String.valueOf(Math.abs(currentDebit)));


                Comparator c = Collections.reverseOrder();
                Collections.sort(transactionsModelArrayList,c);

                transactionsAdapter = new TransactionsAdapter(getContext(), transactionsModelArrayList);
                transactions.setAdapter(transactionsAdapter);
            }
        });
    }

    @Override
    public void sendInput(String startDate, String endDate) {

        if(startDate!=null && endDate != null){
            startDateExisting = startDate;
            endDateExisting = endDate;
            getSortedTransactions(startDate,endDate);
        }
        else {
            getTransactions();
        }

    }
}