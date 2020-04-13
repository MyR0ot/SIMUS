package logic;

import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.GoalType;
import org.knowm.xchart.*;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class MainForm extends JFrame {


    private JButton button1;
    private JPanel panel1;
    private JTable table1;
    private JScrollPane scrollPan1;
    private JButton button2;
    private JButton button4;
    private JSpinner spinner1;
    private JSpinner spinner2;

    private InputData inputData;

    private int maxCriteriaCount = 7;
    private int maxAlternativeCount = 15;

    public MainForm() {
        setContentPane(panel1);

        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        setTitle("SIMUS standalone");
        setVisible(true);

        spinner1.setValue(7);
        spinner2.setValue(12);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // start of code for horizontal headers
                ListModel lm = new AbstractListModel() {
                    String headers[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i"};

                    public int getSize() { return inputData.criteriaCount(); }

                    public Object getElementAt(int index) {
                        return "C-"+ index;
                    }
                };

                JList rowHeader = new JList(lm);
                rowHeader.setFixedCellWidth(50);
                rowHeader.setCellRenderer(new RowHeaderRenderer(table1));
                rowHeader.setFixedCellHeight(table1.getRowHeight());
                scrollPan1.setRowHeaderView(rowHeader);
                // end of code for horizontal headers

                int criteriaCount = (int)spinner1.getValue();
                int alternativeCount = (int)spinner2.getValue();


                inputData = Init.generateRndData(new ConstraintData(criteriaCount, alternativeCount, 0, 100, 5000, 14000*alternativeCount), 2);
                TableModel model = new InputDataTableModel(inputData);
                table1.setModel(model);

                table1.getColumnModel().getColumn(inputData.alternativeCount() ).setCellEditor(new DefaultCellEditor(new JComboBox(new String[] {">","<",">=","<=","="})));
                table1.getColumnModel().getColumn(inputData.alternativeCount() +2).setCellEditor(new DefaultCellEditor(new JComboBox(new String[] {"MAX","MIN"})));

                System.out.println("generate");
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SIMUS simus = new SIMUS(inputData);
                if(simus.runLogic()){
                    Rank[] ranks = simus.getRanks();

                    StringBuilder stringRanks = new StringBuilder();
                    for (int i = 0; i < ranks.length; i++) {
                        System.out.println(ranks[i].minRank + "-" + ranks[i].maxRank);
                        stringRanks.append(ranks[i].minRank).append("-").append(ranks[i].maxRank).append('\n');
                    }
                    JOptionPane.showMessageDialog(MainForm.this, stringRanks); // TODO: сделать другую визализацию
                } else {
                    JOptionPane.showMessageDialog(MainForm.this, "The solution is not found!");
                }
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                // TODO: получить iosaConstraint
                IOSAConstraint iosaConstraint = new IOSAConstraint(
                        multIDM(inputData.idm, 0.95),
                        multIDM(inputData.idm, 1.05),
                        inputData.rhs,
                        inputData.rhs); // TODO: передать 2 idm, 2 rhs;

                IOSAResult iosaResult = SIMUS.runIOSA(inputData, iosaConstraint, 20, 1); // TODO: настроить testCount, successCountMin
                System.err.println("successCount = " + iosaResult.getSuccessCount()); // TODO: kill this
                if (iosaResult.getIsSuccess()) {
                    iosaResult.printPMatrix();
                    showPieChart(iosaResult, 0);
                } else {
                    JOptionPane.showMessageDialog(MainForm.this, "The solution is not found!");
                }
            }
        });

        reCreateTable();
    }

    public static void main(String[] args) {
        new MainForm();
    }

    private void reCreateTable(){
        // start of code for horizontal headers
        ListModel lm = new AbstractListModel() {
            public int getSize() {
                return inputData.criteriaCount();
            }

            public Object getElementAt(int index) {
                return "C-" + index;
            }
        };

        JList rowHeader = new JList(lm);
        rowHeader.setFixedCellWidth(50);
        rowHeader.setCellRenderer(new RowHeaderRenderer(table1));
        rowHeader.setFixedCellHeight(table1.getRowHeight());
        scrollPan1.setRowHeaderView(rowHeader);
        // end of code for horizontal headers

        int criteriaCount = (int) spinner1.getValue();
        int alternativeCount = (int) spinner2.getValue();


        inputData = Init.generateRndData(new ConstraintData(criteriaCount, alternativeCount, 0, 100, 5000, 14000 * alternativeCount), 2);
        TableModel model = new InputDataTableModel(inputData);
        table1.setModel(model);

        JComboBox b = new JComboBox(new String[]{">", "<", ">=", "<=", "="});

        table1.getColumnModel().getColumn(inputData.alternativeCount()).setCellEditor(new DefaultCellEditor(b));
    }


    private PieChart createChart(IOSAResult iosaResult, int alternativeNumber){
        PieChart chart = new PieChartBuilder().width(800).height(600).title("success rate: " + Support.round(iosaResult.getSuccessCount() * 100 / iosaResult.getTestCount(), 2) + "%").theme(Styler.ChartTheme.GGPlot2).build();

        // Customize Chart
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setAnnotationType(PieStyler.AnnotationType.LabelAndPercentage);
        chart.getStyler().setAnnotationDistance(1.15);
        chart.getStyler().setPlotContentSize(.7);
        chart.getStyler().setStartAngleInDegrees(0);

        // Series
        for (int i = 0; i < iosaResult.getPMatrix().length; i++) {
            chart.addSeries("" + (i+1), iosaResult.getPMatrix()[alternativeNumber][i]);

        }
        return chart;
    }

    public void showPieChart(IOSAResult iosaResult, int alternativeNumber) {
        iosaResult.printPMatrix();

        final PieChart chart = createChart(iosaResult, alternativeNumber);


        // Create and set up the window.
        JFrame frame = new JFrame("IOSA");

        JLabel jl_rank = new JLabel("A-");
        JSpinner jsp_rank = new JSpinner();
        jsp_rank.setValue(alternativeNumber + 1);

        JPanel infoPanel = new JPanel();
        infoPanel.setSize(800, 100);
        infoPanel.add(jl_rank);
        infoPanel.add(jsp_rank);


        // TODO:
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // chart
        JPanel chartPanel = new XChartPanel<PieChart>(chart);


        frame.add(chartPanel, BorderLayout.CENTER);
        frame.add(infoPanel, BorderLayout.NORTH);

        jsp_rank.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner) e.getSource();
                int value = (int)spinner.getValue();
                try{
                    System.err.println(value);
                    if(value >= 1 && value <= iosaResult.getPMatrix().length){
                        frame.dispose();
                        showPieChart(iosaResult, value - 1);
                    } else{
                        jsp_rank.setValue(1);
                    }
                } catch (Exception ex){
                    // wtf
                }
            }
        });

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    @Deprecated
    public static double[][] multIDM(double[][] idm, double kef) {
        double[][] res = idm.clone();
        for (int i = 0; i < idm.length; i++) {
            res[i] = idm[i].clone();
        }

        for (int i = 0; i < res.length; i++)
            for (int j = 0; j < res[i].length; j++)
                res[i][j] *= kef;

        return res;
    }

    class RowHeaderRenderer extends JLabel implements ListCellRenderer {

        RowHeaderRenderer(JTable table) {
            JTableHeader header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value, int index, boolean isSelected, boolean cellHasFocus) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    private String consTypeToStr(ConsType type) {
        switch (type) {
            case LE:
                return "<=";
            case GE:
                return ">=";
            case LOWER:
                return "<";
            case UPPER:
                return ">";
            case EQ:
                return "=";
            default:
                return null;
        }

    }

    private ConsType strToConsType(String str) {
        switch (str) {
            case "<=":
                return ConsType.LE;
            case ">=":
                return ConsType.GE;
            case "<":
                return ConsType.LOWER;
            case ">":
                return ConsType.UPPER;
            case "=":
                return ConsType.EQ;
            default:
                return null;
        }

    }

    public class InputDataTableModel implements TableModel {

        private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
        private InputData inputData;

        public InputDataTableModel(InputData inputData) {
            this.inputData = inputData;
        }

        @Override
        public int getRowCount() {
            return inputData.criteriaCount();
        }

        @Override
        public int getColumnCount() {
            return inputData.alternativeCount() + 3; //idm + rhsSign + rhs + actions
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == inputData.alternativeCount()) //rhs sign
                return "rhs sign";
            else if (columnIndex == inputData.alternativeCount() + 1)
                return "rhs";
            else if (columnIndex == inputData.alternativeCount() + 2)
                return "action";
            else
                return "A-" + columnIndex;

        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == inputData.alternativeCount()) //rhs sign
                return String.class;
            else if (columnIndex == inputData.alternativeCount() + 1)
                return Double.class;
            else if (columnIndex == inputData.alternativeCount() + 2)
                return String.class;
            else
                return Double.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == inputData.alternativeCount()) //rhs sign
                return consTypeToStr(inputData.rhsSigns[rowIndex]);
            else if (columnIndex == inputData.alternativeCount() + 1)
                return inputData.rhs[rowIndex];
            else if (columnIndex == inputData.alternativeCount() + 2)
                return inputData.actions[rowIndex].toString();
            else
                return inputData.idm[rowIndex][columnIndex];
        }

        @Override
        public void setValueAt(Object o, int rowIndex, int columnIndex) {
            if (columnIndex == inputData.alternativeCount())
                inputData.rhsSigns[rowIndex] = strToConsType((String) o);
            else if (columnIndex == inputData.alternativeCount() + 1)
                inputData.rhs[rowIndex] = Double.parseDouble(o.toString());
            else if (columnIndex == inputData.alternativeCount() + 2) {
                if (GoalType.valueOf(((String)o).toUpperCase()) == GoalType.MAX)
                    inputData.actions[rowIndex] = GoalType.MAX;
                else if (GoalType.valueOf(((String)o).toUpperCase()) == GoalType.MIN)
                    inputData.actions[rowIndex] = GoalType.MIN;
                else
                    throw new NullPointerException();
            }
            else
                inputData.idm[rowIndex][columnIndex] = Double.parseDouble(o.toString());
        }

        @Override
        public void addTableModelListener(TableModelListener tableModelListener) {
            listeners.add(tableModelListener);
        }

        @Override
        public void removeTableModelListener(TableModelListener tableModelListener) {
            listeners.remove(tableModelListener);
        }
    }
}
