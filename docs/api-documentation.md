# Seating Arrangement System API Documentation

## Base URL
```
http://localhost:8080/api/seating
```

## Endpoints

### 1. Home Endpoint
Used to test if the API is running.

- **URL:** `/` or ``
- **Method:** `GET`
- **Response:**
  ```json
  {
    "message": "Welcome to the Seating Arrangement System API!"
  }
  ```
- **Status Codes:**
  - 200: Success
  - 500: Server Error

### 2. Generate Seating Arrangement
Generates a PDF with seating arrangements based on input files.

- **URL:** `/generate-pdf`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data`

#### Request Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| studentsFile | File (Excel) | Yes | Excel file containing student details |
| roomsFile | File (Excel) | Yes | Excel file containing room details |
| type | String | Yes | Type of arrangement ('alphabetical' or 'rollnumber') |

#### Excel File Formats

**Students File Format:**
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| Name | String | Student's full name |
| RollNumber | String | Student's roll number |
| Class | String | Student's class/section |

**Rooms File Format:**
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| RoomNumber | String | Unique room identifier |
| Capacity | Number | Number of seats in room |
| Floor | Number | Floor number (optional) |

#### Response
- **Success Response:**
  ```json
  {
    "message": "PDF generated successfully!",
    "pdfPath": "/path/to/generated/pdf"
  }
  ```
- **Error Response:**
  ```json
  {
    "error": "Error message details",
    "status": 500
  }
  ```
- **Status Codes:**
  - 200: Success
  - 400: Bad Request (invalid files or parameters)
  - 500: Server Error

## Sample API Usage (JavaScript/React)

```javascript
// Example using Fetch API
const generateSeatingArrangement = async (studentsFile, roomsFile, type) => {
  const formData = new FormData();
  formData.append('studentsFile', studentsFile);
  formData.append('roomsFile', roomsFile);
  formData.append('type', type);

  try {
    const response = await fetch('http://localhost:8080/api/seating/generate-pdf', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
};

// Example React component
import React, { useState } from 'react';

function SeatingArrangementForm() {
  const [studentsFile, setStudentsFile] = useState(null);
  const [roomsFile, setRoomsFile] = useState(null);
  const [type, setType] = useState('alphabetical');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const result = await generateSeatingArrangement(
        studentsFile,
        roomsFile,
        type
      );
      alert('PDF generated successfully!');
    } catch (error) {
      alert('Error generating PDF');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Students File (Excel):
          <input
            type="file"
            accept=".xlsx,.xls"
            onChange={(e) => setStudentsFile(e.target.files[0])}
            required
          />
        </label>
      </div>
      <div>
        <label>Rooms File (Excel):
          <input
            type="file"
            accept=".xlsx,.xls"
            onChange={(e) => setRoomsFile(e.target.files[0])}
            required
          />
        </label>
      </div>
      <div>
        <label>Arrangement Type:
          <select
            value={type}
            onChange={(e) => setType(e.target.value)}
          >
            <option value="alphabetical">Alphabetical</option>
            <option value="rollnumber">Roll Number</option>
          </select>
        </label>
      </div>
      <button type="submit" disabled={loading}>
        {loading ? 'Generating...' : 'Generate Seating Arrangement'}
      </button>
    </form>
  );
}

export default SeatingArrangementForm;
```

## Error Handling
The API uses standard HTTP status codes:
- 200: Successful operation
- 400: Bad request (invalid input)
- 404: Resource not found
- 500: Server error

All error responses include a message explaining the error.

## Notes for Frontend Development
1. Enable CORS on the frontend if needed
2. Handle file size limitations
3. Implement proper error handling
4. Show loading states during API calls
5. Validate file types before submission

## Data Validation
- Excel files must match the specified format
- Type parameter must be either 'alphabetical' or 'rollnumber'
- Files should not exceed server limits (typically 10MB)
