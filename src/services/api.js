import axios from 'axios';

const api = {
  // File encryption/decryption endpoints
  encryptFile: (file, password) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('password', password);
    
    return axios.post('/api/files/encrypt', formData, {
      responseType: 'blob',
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  
  decryptFile: (fileId, password) => {
    return axios.post(`/api/files/decrypt/${fileId}`, { password }, {
      responseType: 'blob'
    });
  },
  
  // This original method is kept for backward compatibility with your DecryptForm component
  decryptUploadedFile: (file, password, originalFileName) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('password', password);
    formData.append('originalFileName', originalFileName);
    
    return axios.post('/api/files/decrypt', formData, {
      responseType: 'blob',
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  
  // File management endpoints
  getMyFiles: () => {
    return axios.get('/api/files/my-files');
  },
  
  downloadEncryptedFile: (fileId) => {
    return axios.get(`/api/files/download/${fileId}`, {
      responseType: 'blob'
    });
  },
  
  deleteFile: (fileId) => {
    return axios.delete(`/api/files/${fileId}`);
  },
  
  // Shared files endpoints
  getSharedWithMe: () => {
    return axios.get('/api/share/shared-with-me');
  },
  
  getSharedFileInfo: (shareId) => {
    return axios.get(`/api/share/${shareId}/info`);
  },
  
  decryptSharedFile: (shareId, password) => {
    return axios.post(`/api/share/${shareId}/decrypt`, { password }, {
      responseType: 'blob'
    });
  }
};

export default api;