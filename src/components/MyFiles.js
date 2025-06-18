import React, { useState, useEffect } from 'react';
import api from '../services/api';

function MyFiles() {
  const [files, setFiles] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [decryptPassword, setDecryptPassword] = useState('');
  const [decryptingFileId, setDecryptingFileId] = useState(null);

  useEffect(() => {
    fetchMyFiles();
  }, []);

  const fetchMyFiles = async () => {
    try {
      setIsLoading(true);
      const response = await api.getMyFiles();
      setFiles(response.data);
      setError('');
    } catch (err) {
      setError('Failed to fetch your files');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDecrypt = async (fileId, fileName) => {
    try {
      setDecryptingFileId(fileId);
      const response = await api.decryptFile(fileId, decryptPassword);
      
      // Create download link for decrypted file
      const downloadUrl = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = downloadUrl;
      
      // Remove the .encrypted extension if it exists
      const originalFileName = fileName.endsWith('.encrypted') 
        ? fileName.substring(0, fileName.length - 10) 
        : fileName;
        
      link.setAttribute('download', originalFileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
      
      setDecryptPassword('');
      setDecryptingFileId(null);
    } catch (err) {
      setError('Failed to decrypt file. Please check your password and try again.');
      console.error(err);
      setDecryptingFileId(null);
    }
  };

  const handleOpenEncrypted = async (fileId, fileName) => {
    try {
      const response = await api.downloadEncryptedFile(fileId);
      
      // Create download link for encrypted file
      const downloadUrl = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      setError('Failed to download encrypted file');
      console.error(err);
    }
  };
  
  const handleDeleteFile = async (fileId) => {
    if (window.confirm('Are you sure you want to delete this file?')) {
      try {
        await api.deleteFile(fileId);
        // Refresh the file list
        fetchMyFiles();
      } catch (err) {
        setError('Failed to delete file');
        console.error(err);
      }
    }
  };

  if (isLoading) {
    return (
      <div className="text-center my-5">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="card-body">
        <h5 className="card-title">My Encrypted Files</h5>
        
        {error && <div className="alert alert-danger">{error}</div>}
        
        {files.length === 0 ? (
          <div className="alert alert-info">
            You don't have any encrypted files yet. Use the Encrypt tab to encrypt files.
          </div>
        ) : (
          <div className="table-responsive">
            <table className="table table-striped table-hover">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>Encryption Key</th>
                  <th>Date Encrypted</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {files.map(file => (
                  <tr key={file.id}>
                    <td>{file.fileName}</td>
                    <td>
                      <code className="user-select-all">{file.encryptionKey}</code>
                    </td>
                    <td>{new Date(file.createdAt).toLocaleString()}</td>
                    <td>
                      <div className="d-flex gap-2">
                        <button 
                          className="btn btn-sm btn-primary"
                          onClick={() => handleOpenEncrypted(file.id, file.fileName)}
                        >
                          Download
                        </button>
                        
                        {decryptingFileId === file.id ? (
                          <div className="input-group">
                            <input
                              type="password"
                              className="form-control form-control-sm"
                              placeholder="Password"
                              value={decryptPassword}
                              onChange={(e) => setDecryptPassword(e.target.value)}
                            />
                            <button 
                              className="btn btn-sm btn-success"
                              onClick={() => handleDecrypt(file.id, file.fileName)}
                            >
                              Confirm
                            </button>
                            <button 
                              className="btn btn-sm btn-outline-secondary"
                              onClick={() => {
                                setDecryptingFileId(null);
                                setDecryptPassword('');
                              }}
                            >
                              Cancel
                            </button>
                          </div>
                        ) : (
                          <>
                            <button 
                              className="btn btn-sm btn-success"
                              onClick={() => setDecryptingFileId(file.id)}
                            >
                              Decrypt
                            </button>
                            <button 
                              className="btn btn-sm btn-danger"
                              onClick={() => handleDeleteFile(file.id)}
                            >
                              Delete
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default MyFiles;