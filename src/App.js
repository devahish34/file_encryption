import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import DecryptShared from './components/DecryptShared';
import './App.css';

// Protected route component
function ProtectedRoute({ children }) {
  const { currentUser, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="d-flex justify-content-center my-5">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }
  
  if (!currentUser) {
    return <Navigate to="/login" />;
  }
  
  return children;
}

// Public route component - redirects to dashboard if logged in
function PublicRoute({ children }) {
  const { currentUser, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="d-flex justify-content-center my-5">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }
  
  if (currentUser) {
    return <Navigate to="/dashboard" />;
  }
  
  return children;
}

// Main App component
function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="d-flex flex-column min-vh-100">
          <header className="bg-dark text-white py-3">
            <div className="container">
              <h1 className="mb-0">FileEncrypt</h1>
              <p className="mb-0 text-white-50">Secure File Encryption & Sharing</p>
            </div>
          </header>

          <main className="flex-grow-1">
            <Routes>
              <Route path="/" element={
                <PublicRoute>
                  <div className="container mt-5">
                    <div className="row">
                      <div className="col-md-6">
                        <h2>Protect Your Files</h2>
                        <p className="lead">
                          FileEncrypt provides secure AES-256 encryption for your sensitive documents.
                          Sign up to store, encrypt, and share files safely.
                        </p>
                        <a href="/login" className="btn btn-primary btn-lg">Get Started</a>
                      </div>
                      <div className="col-md-6 text-center">
                        {/* Placeholder for an image */}
                        <img 
                          src="/api/placeholder/600/400"
                          alt="File encryption illustration"
                          className="img-fluid rounded shadow"
                        />
                      </div>
                    </div>
                  </div>
                </PublicRoute>
              } />
              
              <Route path="/login" element={
                <PublicRoute>
                  <Login />
                </PublicRoute>
              } />
              
              <Route path="/dashboard" element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              } />
              
              <Route path="/shared/:shareId" element={<DecryptShared />} />
              
              <Route path="*" element={<Navigate to="/" />} />
            </Routes>
          </main>

          <footer className="bg-light py-3 mt-auto">
            <div className="container text-center">
              <p className="mb-0 text-muted">
                &copy; {new Date().getFullYear()} FileEncrypt Service | Files are processed securely with AES-256 encryption
              </p>
            </div>
          </footer>
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;