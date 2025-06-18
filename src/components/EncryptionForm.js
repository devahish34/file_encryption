import React, { useState } from 'react';
import api from '../services/api';

function EncryptionForm() {
  const [file, setFile] = useState(null);
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!file || !password) {
      setError('Please select a file and enter a password');
      return;
    }
    
    setIsLoading(true);
    setError('');
    
    try {
      const response = await api.encryptFile(file, password);
      
      // Create download link
      const downloadUrl = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.setAttribute('download', `${file.name}.encrypted`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      setError('Failed to encrypt file. Please try again.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">Encrypt File</h5>
        
        {error && <div className="alert alert-danger">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="encryptFile" className="form-label">Select File</label>
            <input 
              type="file" 
              className="form-control" 
              id="encryptFile" 
              onChange={handleFileChange}
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="encryptPassword" className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              id="encryptPassword"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter encryption password"
            />
          </div>
          
          <button 
            type="submit" 
            className="btn btn-success w-100" 
            disabled={isLoading}
          >
            {isLoading ? (
              <span>
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                {' '}Encrypting...
              </span>
            ) : (
              'Encrypt File'
            )}
          </button>
        </form>
      </div>
    </div>
  );
}

export default EncryptionForm;