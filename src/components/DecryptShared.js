import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../services/api';

function DecryptShared() {
  const { shareId } = useParams();
  const [fileInfo, setFileInfo] = useState(null);
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [decrypting, setDecrypting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    const fetchSharedFileInfo = async () => {
      try {
        const response = await api.getSharedFileInfo(shareId);
        setFileInfo(response.data);
      } catch (err) {
        setError('Invalid or expired share link');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchSharedFileInfo();
  }, [shareId]);

  const handleDecrypt = async (e) => {
    e.preventDefault();
    
    if (!password) {
      setError('Please enter the decryption password');
      return;
    }
    
    setDecrypting(true);
    setError('');
    setSuccess('');
    
    try {
      const response = await api.decryptSharedFile(shareId, password);
      
      // Create download link
      const downloadUrl = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.setAttribute('download', fileInfo.originalFileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      setSuccess('File decrypted successfully!');
    } catch (err) {
      setError('Failed to decrypt file. Please check your password and try again.');
      console.error(err);
    } finally {
      setDecrypting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (!fileInfo && !isLoading) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger">
          {error || 'Invalid or expired share link'}
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow">
            <div className="card-body p-4">
              <h3 className="text-center mb-4">Decrypt Shared File</h3>
              
              {error && <div className="alert alert-danger">{error}</div>}
              {success && <div className="alert alert-success">{success}</div>}
              
              <div className="mb-4">
                <p><strong>File Name:</strong> {fileInfo.fileName}</p>
                <p><strong>Shared By:</strong> {fileInfo.sharedBy}</p>
                {fileInfo.message && (
                  <div className="alert alert-info">
                    <strong>Message:</strong> {fileInfo.message}
                  </div>
                )}
              </div>
              
              <form onSubmit={handleDecrypt}>
                <div className="mb-3">
                  <label htmlFor="password" className="form-label">Decryption Password</label>
                  <input
                    type="password"
                    className="form-control"
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter the decryption password"
                    required
                  />
                </div>
                
                <button 
                  type="submit" 
                  className="btn btn-primary w-100" 
                  disabled={decrypting}
                >
                  {decrypting ? (
                    <span>
                      <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                      {' '}Decrypting...
                    </span>
                  ) : (
                    'Decrypt & Download'
                  )}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DecryptShared;