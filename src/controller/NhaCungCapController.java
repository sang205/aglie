package controller;

import dao.NhaCungCapDAO;
import entities.NhaCungCap;
import gui.page.NhaCungCapPage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.MessageDialog;
import utils.Validation;
import static utils.Validation.isPhoneNumber;

/**
 *
 * @author HP
 */
public class NhaCungCapController extends InterfaceController<NhaCungCap, String> {

    public NhaCungCapDAO NCC_DAO = new NhaCungCapDAO();
    public NhaCungCapPage NCC_GUI;

    public NhaCungCapController() {
    }

    public NhaCungCapController(NhaCungCapPage KH_GUI) {
        this.NCC_GUI = KH_GUI;
    }

    @Override
    public void create(NhaCungCap e) {
        NCC_DAO.create(e);
    }

    @Override
    public void update(NhaCungCap e) {
        NCC_DAO.update(e);
    }

    @Override
    public void deleteById(String id) {
        NCC_DAO.deleteById(id);
    }

    @Override
    public List<NhaCungCap> getAllList() {
        return NCC_DAO.selectAll();
    }

    @Override
    public NhaCungCap selectById(String id) {
        return NCC_DAO.selectById(id);
    }

    public List<NhaCungCap> getSearchTable(String text, String searchType) {
        text = text.toLowerCase();
        List result = new ArrayList<NhaCungCap>();

        switch (searchType) {
            case "Tất cả" -> {
                for (NhaCungCap e : this.getAllList()) {
                    if (e.getId().toLowerCase().contains(text)
                            || e.getTen().toLowerCase().contains(text)
                            || e.getSdt().toLowerCase().contains(text)) {
                        result.add(e);
                    }
                }
            }
            case "Mã" -> {
                for (NhaCungCap e : this.getAllList()) {
                    if (e.getId().toLowerCase().contains(text)) {
                        result.add(e);
                    }
                }
            }
            case "Tên" -> {
                for (NhaCungCap e : this.getAllList()) {
                    if (e.getTen().toLowerCase().contains(text)) {
                        result.add(e);
                    }
                }
            }
            case "Số điện thoại" -> {
                for (NhaCungCap e : this.getAllList()) {
                    if (e.getSdt().toLowerCase().contains(text)) {
                        result.add(e);
                    }
                }
            }
            default ->
                throw new AssertionError();
        }

        return result;
    }

    public void importExcel() {
        File excelFile;
        FileInputStream excelFIS = null;
        BufferedInputStream excelBIS = null;
        XSSFWorkbook excelJTableImport = null;
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Open file");
        FileNameExtensionFilter fnef = new FileNameExtensionFilter("EXCEL FILES", "xls", "xlsx", "xlsm");
        jf.setFileFilter(fnef);
        int result = jf.showOpenDialog(null);

        int check = 0;
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                excelFile = jf.getSelectedFile();
                excelFIS = new FileInputStream(excelFile);
                excelBIS = new BufferedInputStream(excelFIS);
                excelJTableImport = new XSSFWorkbook(excelBIS);
                XSSFSheet excelSheet = excelJTableImport.getSheetAt(0);

                for (int row = 1; row <= excelSheet.getLastRowNum(); row++) {
                    XSSFRow excelRow = excelSheet.getRow(row);

                    if (excelRow == null || excelRow.getCell(0) == null || excelRow.getCell(1) == null
                            || excelRow.getCell(2) == null || excelRow.getCell(3) == null) {
                        check++;
                        continue;
                    }

                    String id = getCellValueAsString(excelRow.getCell(0));
                    String ten = getCellValueAsString(excelRow.getCell(1));
                    String sdt = getCellValueAsString(excelRow.getCell(2));
                    String diaChi = getCellValueAsString(excelRow.getCell(3));

                    if (Validation.isEmpty(id) || Validation.isEmpty(ten)
                            || Validation.isEmpty(sdt) || !isPhoneNumber(sdt)
                            || Validation.isEmpty(diaChi)) {
                        check++;
                    } else {
                        NhaCungCap ncc = new NhaCungCap(id, ten, sdt, diaChi);
                        NCC_DAO.create(ncc);
                    }
                }

                MessageDialog.info(NCC_GUI, "Nhập dữ liệu thành công!");

            } catch (FileNotFoundException ex) {
                MessageDialog.error(NCC_GUI, "Lỗi đọc file");
            } catch (IOException ex) {
                MessageDialog.error(NCC_GUI, "Lỗi đọc file");
            }
        }

        if (check != 0) {
            MessageDialog.error(NCC_GUI, "Có " + check + " dòng dữ liệu không được thêm vào!");
        }
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // hoặc định dạng theo SimpleDateFormat
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return "";
        }
    }

}
