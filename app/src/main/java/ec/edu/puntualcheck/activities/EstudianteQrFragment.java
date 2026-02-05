package ec.edu.puntualcheck.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import ec.edu.puntualcheck.R;
import ec.edu.puntualcheck.utils.SessionManager;

public class EstudianteQrFragment extends Fragment {

    private ImageView imgQrCodigo;
    private TextView tvNombre;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estudiante_qr, container, false);

        imgQrCodigo = view.findViewById(R.id.imgQrCodigo);
        tvNombre = view.findViewById(R.id.tvNombreEstudiante);
        session = new SessionManager(getContext());

        tvNombre.setText(session.getNombre());
        generarQRReal();

        return view;
    }

    private void generarQRReal() {
        try {
            String idParaQR = String.valueOf(session.getUserId());

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(idParaQR, BarcodeFormat.QR_CODE, 500, 500);
            imgQrCodigo.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}