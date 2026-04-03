# BankGuard Transaction Portal - Frontend

A minimal yet complete React + Vite frontend application for the BankGuard transaction system.

## Features

- **Multi-Step Form** - User-friendly 3-step process:
  1. **Account Creation** - Create a bank account with account number, type, and bank name
  2. **Customer Profile** - Create customer profile with name, email, and risk score
  3. **Transaction Submission** - Submit transaction with source details, location, and IP address

- **Dropdowns for Source Selection**:
  - **Source Type**: Bank, UPI, Wallet, Card
  - **Source Name**: Dynamically populated based on selected source type
    - Bank: HDFC, ICICI, SBI, Axis
    - UPI: Google Pay, PhonePe, Paytm, BHIM
    - Wallet: PayPal, Skrill, Wise, Amazon Pay
    - Card: Visa, Mastercard, American Express

- **Additional Transaction Data**:
  - Location (e.g., city/country)
  - IP Address
  - Automatic timestamp (Date & Time)

- **Minimal, Clean UI**:
  - Gradient background with modern styling
  - Responsive design for mobile and desktop
  - Step indicator showing progress
  - Success/error message notifications

## Technology Stack

- **React 19.2** - UI framework
- **Vite 8** - Build tool and dev server
- **ES Modules** - Modern JavaScript
- **Fetch API** - HTTP requests

## Project Structure

```
clientFrontend/
├── src/
│   ├── App.jsx          # Main component with multi-step form
│   ├── App.css          # Application styling
│   ├── api.js           # API configuration and helpers
│   ├── index.css        # Global styles
│   └── main.jsx         # React entry point
├── index.html           # HTML template
├── vite.config.js       # Vite configuration
└── package.json         # Dependencies
```

## Installation & Setup

### Prerequisites
- Node.js 16+ and npm installed
- TransactionService backend running on `http://localhost:8081`

### Steps

1. **Navigate to the frontend directory**:
   ```bash
   cd clientFrontend
   ```

2. **Install dependencies** (if not already installed):
   ```bash
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   ```

4. **Open in browser**:
   - Navigate to `http://localhost:5173` (or the URL shown in terminal)

## API Integration

The frontend communicates with the Spring Boot backend (`TransactionService`) running on port 8081.

### API Endpoints Used:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/accounts/add` | POST | Create a new account |
| `/api/customers/add/{accountNumber}` | POST | Create customer profile |
| `/api/payloads/add` | POST | Create transaction payload |
| `/api/transactions/add` | POST | Create raw transaction |

## Workflow

1. **Step 1: Account Creation**
   - User enters account number (12-digit)
   - Selects account type (savings/current/business)
   - Enters bank name
   - Account is saved to database

2. **Step 2: Customer Profile**
   - User enters name and email
   - Sets risk score (0-100)
   - Profile is linked to the created account
   - Customer ID is obtained for future transactions

3. **Step 3: Transaction Submission**
   - User selects source type (bank, UPI, wallet, card)
   - Selects specific source name from dropdown
   - Enters transaction location
   - Enters IP address
   - Transaction data is saved to database with:
     - Current date and time
     - Status: "pending"
     - Processing Status: false

4. **Step 4: Success Confirmation**
   - Displays success message with customer ID and account number
   - Option to submit another transaction

## Environment Configuration

The API base URL is configured in `src/api.js`:

```javascript
export const API_BASE_URL = 'http://localhost:8081/api'
```

If your backend is running on a different port, update this file.

## Build for Production

To build the application for production:

```bash
npm run build
```

The built files will be in the `dist` directory, ready to be deployed.

## Styling Details

- **Color Scheme**: Purple gradient (primary: #667eea, secondary: #764ba2)
- **Responsive**: Mobile-friendly design works on screens from 320px wide
- **Animations**: Smooth transitions for buttons and step indicators
- **Accessibility**: Proper form labels and semantic HTML

## Error Handling

The frontend provides user-friendly error messages for:
- Network failures
- Invalid data submissions
- API errors from backend
- Validation errors

## Notes

- All form fields are required
- Account numbers should be 12-digit values
- Risk score must be between 0-100
- IP address format: standard IPv4 (e.g., 192.168.1.1)
- Timestamps are automatically generated based on server time

## Future Enhancements

- Add form validation patterns
- Implement authentication/authorization
- Add transaction history view
- Add edit/delete functionality
- Implement loading skeletons
- Add dark mode support
