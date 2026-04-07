import { createPortal } from 'react-dom';

/**
 * Portal — renders children directly into #modal-root on document.body.
 * This completely bypasses any CSS stacking contexts in the page tree,
 * guaranteeing modals always appear on top of everything.
 */
export default function Portal({ children }) {
  const el = document.getElementById('modal-root');
  if (!el) return children;
  return createPortal(children, el);
}
