import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import EncryptionForm from './EncryptionForm';
import DecryptionForm from './DecryptionForm';
import MyFiles from './MyFiles';
import ShareEmail from './ShareEmail';

function Dashboard() {
  const { currentUser, logout } = useAuth();
  const [activeTab, setActiveTab] = useState('encrypt');
  const [error, setError] = useState('');

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Welcome, {currentUser?.name || 'User'}</h2>
        <button className="btn btn-outline-danger" onClick={logout}>
          Logout
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <ul className="nav nav-tabs mb-4">
        <li className="nav-item">
          <button 
            className={`nav-link ${activeTab === 'encrypt' ? 'active' : ''}`}
            onClick={() => setActiveTab('encrypt')}
          >
            Encrypt
          </button>
        </li>
        <li className="nav-item">
          <button 
            className={`nav-link ${activeTab === 'decrypt' ? 'active' : ''}`}
            onClick={() => setActiveTab('decrypt')}
          >
            Decrypt
          </button>
        </li>
        <li className="nav-item">
          <button 
            className={`nav-link ${activeTab === 'myfiles' ? 'active' : ''}`}
            onClick={() => setActiveTab('myfiles')}
          >
            My Files
          </button>
        </li>
        <li className="nav-item">
          <button 
            className={`nav-link ${activeTab === 'emailShare' ? 'active' : ''}`}
            onClick={() => setActiveTab('emailShare')}
          >
            Email Share
          </button>
        </li>
      </ul>

      <div className="tab-content">
        {activeTab === 'encrypt' && <EncryptionForm />}
        {activeTab === 'decrypt' && <DecryptionForm />}
        {activeTab === 'myfiles' && <MyFiles />}
        {activeTab === 'emailShare' && <ShareEmail />}
      </div>
    </div>
  );
}

export default Dashboard;