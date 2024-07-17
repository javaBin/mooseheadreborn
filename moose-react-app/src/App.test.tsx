import React from 'react';
import { render, screen} from '@testing-library/react';
import {act} from 'react';
import App from './App';
import {WorkshopType} from "./ServerTypes";
//import fetch from 'node-fetch';

test('renders headline on main page', () => {
  render(<App />);
  const linkElement = screen.getByText(/Workshops/i);
  expect(linkElement).toBeInTheDocument();
});

/*
test('renders workshop list on mainpage',async () => {
    const fakeWorkshops:WorkshopType[] = [
        {id: "1", name: "Fake workshop one"},
        {id: "2", name: "Fake workshop two"}
    ];


    const mockFetch = jest.fn(() => {
        Promise.resolve({
            json: () => Promise.resolve(fakeWorkshops)
        })
    });

    (global as any).fetch = mockFetch;

    let container;
    await act(async () => {
        container = render(<App />);
    });

    render(<App/>);
    const workshopTitles = await screen.findAllByRole('heading');
    expect(workshopTitles).toHaveLength(1);
});*/
