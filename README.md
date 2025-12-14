# Billing_System

A compact Android billing/invoicing app (Java + Kotlin) for small shops. This README emphasizes the app's features: what they do, how to use them from the UI, and where the main behavior lives in the code.

I read the repository and produced a feature-focused README below describing each feature, how it works in the app, and which classes implement it.

---

## Quick summary

Billing_System helps you:
- Create sales invoices by selecting products and quantities.
- Generate PDF invoices (saved to device storage) with totals and invoice metadata.
- View, save and share generated PDFs.
- Maintain product stock and update product details.
- Export/import app data (database + shared prefs) as backups.
- Generate history reports of transactions.

---

## Feature details

1. Invoice creation (Sales)
   - What it does
     - Add one or more products (max 10 items per invoice), enter quantities, and create a bill.
     - Validates quantity input and ensures requested quantity does not exceed current stock.
     - Calculates line totals and net amount automatically.
   - Where it's implemented
     - UI & logic: `SalesActivity.java`
     - Dynamic product rows: `addNewView()` creates product-selection rows.
     - Validation: `checkQTY()` and `CheckAllFields()` ensure required fields are present and valid.
   - Usage
     - Open Sales screen → tap "Add" to add product rows (up to 10) → select product from spinner → enter quantity → press the finalise/save button to generate the invoice.

2. PDF invoice generation
   - What it does
     - Builds a PDF invoice using Android's `PdfDocument` API containing store name, customer info, Bill No, date/time, itemized rows (product id, name, qty, rate, amount) and Net Amount.
     - Saves PDF to external storage and records the file path in the local database for later retrieval.
   - Where it's implemented
     - Main generator: `PDF2()` method in `SalesActivity.java`.
     - Number-to-words: `numbertoword.java` helper (used when you want textual representation of amounts).
     - Saves PDF file path via database helper calls in `DataBaseHandler` (DB handler resides in the `DataBase` package).
   - File location
     - Generated PDFs are saved to: `/sdcard/DATA/Invoice{Bill_No}.pdf` (i.e., the DATA folder on external storage).
   - Notes
     - The PDF layout uses fixed coordinates; long product names may need text-wrapping improvements (there is a helper `print_next_line()` in `SalesActivity` to help with wrapping).

3. PDF viewing, saving and sharing
   - What it does
     - Open generated PDFs inside the app, save a copy to Downloads, and share via Android share sheet.
   - Where it's implemented
     - Viewer UI & sharing: `PdfviewActivity.java`
     - Alternate viewer using path: `DocmentActivity.java` (uses `PDFView` lib)
   - Usage
     - After generating an invoice, the app opens `PdfviewActivity` where you can:
       - Save: moves the file to the system Downloads folder.
       - Share: shares PDF via `FileProvider` (ensures safe sharing of files with other apps).

4. Stock management and product updates
   - What it does
     - Product stock is decreased automatically when a sale is completed.
     - You can view and update product quantity and unit cost manually via a product update screen.
   - Where it's implemented
     - Stock update on sale: `update_stock()` in `SalesActivity.java` calls database updates.
     - Update UI: `UpdateActivity2.java` (lists products and allows changing quantity & cost). Uses `UpdateAdapter` and `UpdateListener` to handle updates.
     - Database logic: `DataBaseHandler` (handles stock, transactions and customer inserts).
   - Usage
     - Use the Update Products screen to edit quantity/cost; after saving, the product list refreshes.

5. Transaction history & reports
   - What it does
     - Build history-style PDFs that list transactions/ invoices and aggregate totals for reporting.
   - Where it's implemented
     - `HisPdfviewActivity.java` constructs history reports into a PDF and draws tabular headers and rows.
   - Notes
     - The history report renderer uses fixed coordinates and manages page breaks; review the code if you need additional filtering or export formats.

6. User / simple authentication
   - What it does
     - Basic signup screen to add a username/password entry into the local DB.
     - The app stores the active user in SharedPreferences and attaches this user to transactions.
   - Where it's implemented
     - Signup: `SignUpActivity.java`
     - Shared preferences access: `SalesActivity.java` (reads `USER_KEY` to tag transactions).

7. Import / Export (backup & restore)
   - What it does
     - Export copies of the app database and shared_prefs to external storage, and Import restores from those backup files.
     - Backup location: `/sdcard/DATA/Backup/` (database file `BILLING_SYSTEM.db` and `shared_prefs.xml`).
   - Where it's implemented
     - Export/Import classes: `Export.java` and `Import.java` in `java` package.
     - MainActivity contains UI/permission handling to trigger import/export actions.

8. Utilities
   - numbertoword.java: convert numeric totals to English words (useful for showing "Amount in words" on invoices).
   - PdfViewHolder, UpdateListener: small model/helper classes used by RecyclerViews for history or update screens.

---

## How to use — feature-focused quick steps

1. Prepare app
   - Install and open the app.
   - Ensure storage permissions are granted for saving and sharing PDFs.

2. Add / update products
   - Use Update Products screen to add stock or edit cost (the UI lists products and allows editing via a dialog).

3. Create an invoice
   - Open Sales screen.
   - Tap "Add" to add product line(s).
   - Select product(s) from the spinner; available quantity hint is shown.
   - Enter valid quantities (validation enforces non-zero and not exceeding available stock).
   - Enter Customer Name and Phone.
   - Tap the "Save/Generate" button — PDF will be created, stock adjusted, transaction saved, and PDF opened.

4. View / Save / Share PDF
   - In the PDF viewer, you can save the invoice to Downloads or share it using the share button.

5. Backup / Restore
   - Use the export/import functionality (Main screen) to create backups in `/sdcard/DATA/Backup/` or restore from there.

6. View history
   - Use the History/Reports screen to generate a consolidated PDF of past transactions.

---

## Permissions & platform notes (practical for features)

- The app performs read/write operations on external storage. On Android 6.0+ it requests runtime permissions for storage; on Android 11+ device behavior changes (scoped storage).
- If PDF saving/sharing or import/export fails on modern Android versions, grant the app All-files access or adapt the app to use the Storage Access Framework (recommended long-term).
