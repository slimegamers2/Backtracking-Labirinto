package template;

import br.com.davidbuzatto.jsge.core.engine.EngineFrame;
import br.com.davidbuzatto.jsge.core.utils.DrawingUtils;
import br.com.davidbuzatto.jsge.image.Image;
import java.util.ArrayList;

public class Main extends EngineFrame {

    private Image logo;
    int[][] mazeGrid;
    int[][] visitados;
    ArrayList<int[][]> passos;
    ArrayList<String[][]> direcoes;  // Armazena as direções das setas
    int inicioL, inicioC, fimL, fimC, pos;
    private double tempoParaMudar, contadorTempo;

    public Main() {
        super(500, 500, "Labirinto Visual", 60, true, false, false, false, false, false);
    }

    @Override
    public void create() {
        passos = new ArrayList<>();
        direcoes = new ArrayList<>();  // Inicializa a lista de direções
        logo = DrawingUtils.createLogo();
        logo.resize((int) (logo.getWidth() * 0.1), (int) (logo.getWidth() * 0.1));
        setWindowIcon(logo);

        mazeGrid = new int[][] {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 0, 0, 1, 0, 0, 0, 0, 1},
            {1, 1, 0, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 3, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        visitados = new int[10][10];

        for (int i = 0; i < mazeGrid.length; i++) {
            for (int j = 0; j < mazeGrid[i].length; j++) {
                if (mazeGrid[i][j] == 2) {
                    inicioL = i; inicioC = j;
                }
                if (mazeGrid[i][j] == 3) {
                    fimL = i; fimC = j;
                }
            }
        }

        mover(inicioL, inicioC, fimL, fimC);
        pos = 0;
        tempoParaMudar = 0.5;
    }

    @Override
    public void update(double delta) {
        contadorTempo += delta;
        if (contadorTempo > tempoParaMudar) {
            contadorTempo = 0;
            pos++;
        }
        if (pos >= passos.size()) {
            pos = passos.size() - 1;
        }
    }

    @Override
    public void draw() {
        clearBackground(WHITE); // Muda o fundo para branco para uma aparência mais limpa
        for (int i = 0; i < mazeGrid.length; i++) {
            for (int j = 0; j < mazeGrid[i].length; j++) {
                switch (mazeGrid[i][j]) {
                    case 0 -> fillRectangle(50 * j, 50 * i, 50, 50, LIGHTGRAY); // Caminho livre em cinza claro
                    case 1 -> fillRectangle(50 * j, 50 * i, 50, 50, DARKGRAY); // Paredes em cinza escuro
                    case 2 -> fillRectangle(50 * j, 50 * i, 50, 50, GREEN); // Início em verde
                    case 3 -> fillRectangle(50 * j, 50 * i, 50, 50, RED); // Fim em vermelho
                }
            }
        }

        if (!passos.isEmpty() && pos < passos.size()) {
            desenharAcessados(passos.get(pos), direcoes.get(pos));
        }
    }

    private boolean mover(int linhaFonte, int colunaFonte, int linhaDest, int colunaDest) {
        if (valido(linhaFonte, colunaFonte)) {
            visitados[linhaFonte][colunaFonte] = 1;
            passos.add(copyMatrix(visitados));
            direcoes.add(copyDirecoes());  // Armazena as direções das setas
            if (linhaFonte == linhaDest && colunaFonte == colunaDest) return true;
            
            // Definir a direção do movimento
            if (mover(linhaFonte - 1, colunaFonte, linhaDest, colunaDest)) {
                definirDirecao(linhaFonte, colunaFonte, linhaFonte - 1, colunaFonte); // Mover para cima
                return true;
            }
            if (mover(linhaFonte, colunaFonte + 1, linhaDest, colunaDest)) {
                definirDirecao(linhaFonte, colunaFonte, linhaFonte, colunaFonte + 1); // Mover para a direita
                return true;
            }
            if (mover(linhaFonte + 1, colunaFonte, linhaDest, colunaDest)) {
                definirDirecao(linhaFonte, colunaFonte, linhaFonte + 1, colunaFonte); // Mover para baixo
                return true;
            }
            if (mover(linhaFonte, colunaFonte - 1, linhaDest, colunaDest)) {
                definirDirecao(linhaFonte, colunaFonte, linhaFonte, colunaFonte - 1); // Mover para a esquerda
                return true;
            }
        }
        return false;
    }

    private boolean valido(int line, int column) {
        return line >= 0 && line < mazeGrid.length &&
               column >= 0 && column < mazeGrid[line].length &&
               mazeGrid[line][column] != 1 && visitados[line][column] != 1;
    }

    private int[][] copyMatrix(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    private String[][] copyDirecoes() {
        String[][] copia = new String[mazeGrid.length][mazeGrid[0].length];
        for (int i = 0; i < copia.length; i++) {
            for (int j = 0; j < copia[i].length; j++) {
                copia[i][j] = "";  // Inicializa com uma string vazia
            }
        }
        return copia;
    }

    private void desenharAcessados(int[][] acessado, String[][] direcoes) {
    for (int i = 0; i < acessado.length; i++) {
        for (int j = 0; j < acessado[i].length; j++) {
            if (acessado[i][j] == 1 && !(i == inicioL && j == inicioC)) { // Verifica se não é a célula inicial
                fillRectangle(50 * j, 50 * i, 50, 50, BLUE); // Caminho percorrido em azul
                if (!direcoes[i][j].isEmpty()) {
                    drawText(direcoes[i][j], 50 * j + 15, 50 * i + 15, 20, BLACK); // Desenha a seta
                }
            }
        }
    }
}

    private void definirDirecao(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino) {
        if (linhaDestino < linhaOrigem) {
            direcoes.get(direcoes.size() - 1)[linhaOrigem][colunaOrigem] = "↑"; // Seta para cima
        } else if (linhaDestino > linhaOrigem) {
            direcoes.get(direcoes.size() - 1)[linhaOrigem][colunaOrigem] = "↓"; // Seta para baixo
        } else if (colunaDestino < colunaOrigem) {
            direcoes.get(direcoes.size() - 1)[linhaOrigem][colunaOrigem] = "←"; // Seta para a esquerda
        } else if (colunaDestino > colunaOrigem) {
            direcoes.get(direcoes.size() - 1)[linhaOrigem][colunaOrigem] = "→"; // Seta para a direita
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
