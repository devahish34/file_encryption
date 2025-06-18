import React, { useState } from 'react';
import api from '../services/api';

function DecryptionForm() {
  const [file, setFile] = useState(null);
  const [password, setPassword] = useState('');
  const [originalFileName, setOriginalFileName] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    setFile(selectedFile);
    
    // Extract original file name without .encrypted extension
    if (selectedFile && selectedFile.name.endsWith('.encrypted')) {
      setOriginalFileName(selectedFile.name.substring(0, selectedFile.name.length - 10));
    } else {
      setOriginalFileName(selectedFile ? selectedFile.name : '');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!file || !password || !originalFileName) {
      setError('Please select a file, enter a password, and provide the original file name');
      return;
    }
    
    setIsLoading(true);
    setError('');
    
    try {
      const response = await api.decryptFile(file, password, originalFileName);
      
      // Create download link
      const downloadUrl = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.setAttribute('download', originalFileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      setError('Failed to decrypt file. Please check your password and try again.');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">Decrypt File</h5>
        
        {error && <div className="alert alert-danger">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="decryptFile" className="form-label">Select Encrypted File</label>
            <input 
              type="file" 
              className="form-control" 
              id="decryptFile" 
              onChange={handleFileChange}
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="decryptPassword" className="form-label">Password</label>
            <input
              type="password"
              className="form-control"
              id="decryptPassword"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter decryption password"
            />
          </div>
          
          <div className="mb-3">
            <label htmlFor="originalFileName" className="form-label">Original File Name</label>
            <input
              type="text"
              className="form-control"
              id="originalFileName"
              value={originalFileName}
              onChange={(e) => setOriginalFileName(e.target.value)}
              placeholder="Enter original file name"
            />
          </div>
          
          <button 
            type="submit" 
            className="btn btn-primary w-100" 
            disabled={isLoading}
          >
            {isLoading ? (
              <span>
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                {' '}Decrypting...
              </span>
            ) : (
              'Decrypt File'
            )}
          </button>
        </form>
      </div>
    </div>
  );
}

export default DecryptionForm;