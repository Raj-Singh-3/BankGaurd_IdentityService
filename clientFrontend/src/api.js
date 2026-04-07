export const API_BASE_URL = 'http://localhost:8081/api'

export const API_ENDPOINTS = {
  ACCOUNTS: `${API_BASE_URL}/accounts`,
  CUSTOMERS: `${API_BASE_URL}/customers`,
  PAYLOADS: `${API_BASE_URL}/payloads`,
  TRANSACTIONS: `${API_BASE_URL}/transactions`,
  EVALUATED_TRANSACTIONS: `${API_BASE_URL}/evaluated-transactions`
}

export const handleApiCall = async (url, method = 'GET', data = null) => {
  const options = {
    method,
    headers: {
      'Content-Type': 'application/json'
    }
  }

  if (data) {
    options.body = JSON.stringify(data)
  }

  try {
    const response = await fetch(url, options)
    if (!response.ok) {
      throw new Error(`API error: ${response.statusText}`)
    }
    return await response.json()
  } catch (error) {
    throw error
  }
}
