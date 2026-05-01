import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.fasterxml.jackson.databind.ObjectMapper;

import entidades.Artista;
import entidades.Artistas;
import entidades.Cancion;

public class FrmReproductor extends JFrame {

    private JTree arbol;
    DefaultMutableTreeNode nodoRaiz;
    JLabel lblFoto;
    JPanel pnlFoto, pnlLetra;
    JTextArea txtLetra;

    // Ruta a la carpeta de recursos (relativa al directorio donde se ejecuta el .jar)
    private static final String RUTA_BASE = "recursos/";

    private Artistas artistas;

    public FrmReproductor() {
        setSize(700, 500);
        setTitle("Musiteca 🎵");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ── Barra de herramientas ──────────────────────────────────────────────
        JToolBar tbArtista = new JToolBar();
        tbArtista.setFloatable(false);

        JButton btnReproducir = new JButton();
        btnReproducir.setIcon(new ImageIcon(getClass().getResource("/iconos/Reproducir.png")));
        btnReproducir.setToolTipText("Reproducir canción seleccionada");
        btnReproducir.addActionListener(evt -> btnReproducir());
        tbArtista.add(btnReproducir);

        JButton btnDetener = new JButton("⏹ Detener");
        btnDetener.setToolTipText("Detener reproducción");
        btnDetener.addActionListener(evt -> {
            ReproductorAudio.detener();
            setTitle("Musiteca 🎵");
        });
        tbArtista.add(btnDetener);

        // ── Árbol de artistas/canciones ───────────────────────────────────────
        nodoRaiz = new DefaultMutableTreeNode("Artistas");
        arbol = new JTree(new DefaultTreeModel(nodoRaiz));
        JScrollPane spArbol = new JScrollPane(arbol);

        cargarArbol();

        arbol.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                mostrarFoto();
                mostrarInfo();
            }
        });

        // ── Panel derecho ──────────────────────────────────────────────────────
        pnlFoto = new JPanel(new BorderLayout());
        pnlLetra = new JPanel(new BorderLayout());

        lblFoto = new JLabel("", JLabel.CENTER);
        pnlFoto.add(lblFoto, BorderLayout.CENTER);

        txtLetra = new JTextArea();
        txtLetra.setEditable(false);
        txtLetra.setLineWrap(true);
        txtLetra.setWrapStyleWord(true);
        txtLetra.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));

        JScrollPane spLetra = new JScrollPane(txtLetra);
        pnlLetra.add(spLetra, BorderLayout.CENTER);

        JTabbedPane tpDatos = new JTabbedPane();
        tpDatos.addTab("📷 Foto", pnlFoto);
        tpDatos.addTab("ℹ Información", pnlLetra);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spArbol, tpDatos);
        splitPane.setDividerLocation(250);

        getContentPane().add(tbArtista, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    // ── Carga el JSON y construye el árbol ────────────────────────────────────
    private void cargarArbol() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File(RUTA_BASE + "Musiteca.json");
            artistas = mapper.readValue(jsonFile, Artistas.class);

            for (Artista artista : artistas.getArtistas()) {
                DefaultMutableTreeNode nodoArtista = new DefaultMutableTreeNode(artista.getNombre());
                for (Cancion cancion : artista.getCanciones()) {
                    nodoArtista.add(new DefaultMutableTreeNode(cancion.getTitulo()));
                }
                nodoRaiz.add(nodoArtista);
            }

            for (int i = 0; i < arbol.getRowCount(); i++) {
                arbol.expandRow(i);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar Musiteca.json:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Muestra la foto del artista ───────────────────────────────────────────
    private void mostrarFoto() {
        lblFoto.setIcon(null);
        lblFoto.setText("");

        Artista artista = obtenerArtistaSeleccionado();
        if (artista == null) return;

        File imgFile = new File(RUTA_BASE + nombreArchivoArtista(artista.getNombre()) + ".jpeg");
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH);
            lblFoto.setIcon(new ImageIcon(scaled));
        } else {
            lblFoto.setText("Sin imagen disponible");
        }
    }

    // ── Muestra información según nodo seleccionado ───────────────────────────
    private void mostrarInfo() {
        txtLetra.setText("");

        TreePath path = arbol.getSelectionPath();
        if (path == null) return;

        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) path.getLastPathComponent();
        int nivel = nodo.getLevel();

        if (nivel == 1) {
            Artista artista = obtenerArtistaSeleccionado();
            if (artista != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("🎤 Artista : ").append(artista.getNombre()).append("\n");
                sb.append("📍 País    : ").append(artista.getPais()).append("\n");
                sb.append("🎵 Tipo    : ").append(artista.getTipo()).append("\n\n");
                sb.append("─────────────────────────\n");
                sb.append(" Canciones disponibles:\n");
                sb.append("─────────────────────────\n");
                for (Cancion c : artista.getCanciones()) {
                    sb.append("  • ").append(c.getTitulo())
                      .append("  (").append(c.getAño()).append(")")
                      .append("  ⏱ ").append(c.getDuracion()).append("\n");
                }
                txtLetra.setText(sb.toString());
            }
        } else if (nivel == 2) {
            Artista artista = obtenerArtistaSeleccionado();
            Cancion cancion = obtenerCancionSeleccionada();
            if (artista != null && cancion != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("🎵 Canción  : ").append(cancion.getTitulo()).append("\n");
                sb.append("🎤 Artista  : ").append(artista.getNombre()).append("\n");
                sb.append("📅 Año      : ").append(cancion.getAño()).append("\n");
                sb.append("⏱ Duración : ").append(cancion.getDuracion()).append("\n");
                sb.append("🎶 Género   : ").append(cancion.getGenero()).append("\n\n");
                sb.append("▶ Presiona el botón Reproducir para escuchar.");
                txtLetra.setText(sb.toString());
            }
        }
    }

    // ── Reproduce la canción seleccionada ─────────────────────────────────────
    private void btnReproducir() {
        Artista artista = obtenerArtistaSeleccionado();
        Cancion cancion = obtenerCancionSeleccionada();

        if (cancion == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una canción del árbol primero.",
                    "Selecciona una canción", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nombreArchivo = RUTA_BASE + nombreArchivoCancion(cancion.getTitulo()) + ".mpeg";
        File audioFile = new File(nombreArchivo);

        if (!audioFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el archivo de audio:\n" + audioFile.getAbsolutePath(),
                    "Archivo no encontrado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ReproductorAudio.reproducir(audioFile.getAbsolutePath());
        setTitle("▶ " + cancion.getTitulo() + "  —  " + artista.getNombre());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Artista obtenerArtistaSeleccionado() {
        if (artistas == null) return null;
        TreePath path = arbol.getSelectionPath();
        if (path == null || path.getPathCount() < 2) return null;
        String nombre = path.getPathComponent(1).toString();
        for (Artista a : artistas.getArtistas()) {
            if (a.getNombre().equals(nombre)) return a;
        }
        return null;
    }

    private Cancion obtenerCancionSeleccionada() {
        Artista artista = obtenerArtistaSeleccionado();
        if (artista == null) return null;
        TreePath path = arbol.getSelectionPath();
        if (path == null || path.getPathCount() < 3) return null;
        String titulo = path.getLastPathComponent().toString();
        for (Cancion c : artista.getCanciones()) {
            if (c.getTitulo().equals(titulo)) return c;
        }
        return null;
    }

    private String nombreArchivoArtista(String nombre) {
        switch (nombre.toLowerCase()) {
            case "bad bunny": return "bad_bunny";
            case "feid":      return "feid";
            case "karol g":   return "karol_g";
            default:          return nombre.toLowerCase().replace(" ", "_");
        }
    }

    private String nombreArchivoCancion(String titulo) {
        switch (titulo.toLowerCase()) {
            case "titi me preguntó": return "Titi_me_pregunto__bad_bunny_";
            case "callaita":         return "Callaita__bad_bunny_";
            case "amor de mi vida":  return "Amor_de_mi_vida__feid_";
            case "la pasamos cabrón":return "La_pasamos_cabron__feid_";
            case "si tu supieras":   return "Si_tu_supieras__feid_";
            case "ay dios mio":      return "Ay_dios_mio__karol_g_";
            case "bichota":          return "bichota__karol_g_";
            case "sejodioto":        return "Sejodiotodo__karol_g_";
            default:                 return titulo.replace(" ", "_");
        }
    }
}
